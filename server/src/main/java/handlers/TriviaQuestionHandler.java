package handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import okhttp3.*;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class TriviaQuestionHandler implements Route {

  private static final String API_KEY =
      System.getenv("OPEN_API_KEY"); // Replace with env var in production
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final StorageInterface storageInterface;

  public TriviaQuestionHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    ArrayList<Object> questionsList = new ArrayList<>();
    HashSet<String> seenQuestions = new HashSet<>();

    String elo = request.queryParams("elo");
    String topic = request.queryParams("topic");

    if (elo == null || topic == null) {
      response.status(400);
      return "Missing required query parameters: 'elo' and/or 'topic'";
    }

    // Parse elo value
    Double eloRanking;
    try {
      eloRanking = Double.parseDouble(elo);
    } catch (NumberFormatException e) {
      response.status(400);
      return "Elo must be a valid number";
    }

    Map<Number, String> difficultyLevel = new HashMap<>();
    difficultyLevel.put(20, "Easy");
    difficultyLevel.put(50, "Medium");
    difficultyLevel.put(100, "Hard");
    difficultyLevel.put(200, "Very Hard");
    difficultyLevel.put(300, "Expert");

    OkHttpClient client = new OkHttpClient();
    while (questionsList.size() < 10) {
      eloRanking = eloRanking + 50;
      String level = getDifficultyLevel(difficultyLevel, eloRanking);

      String prompt =
          """
      Generate  different trivia question in JSON format with these fields:
      {
        "question": "string",
        "options": ["A: Option", "B: Option", "C: Option", "D: Option"],
        "answer": "Correct option from above"
      } """
              + "make the question of difficulty "
              + level
              + " make the question of topic "
              + topic;

      JsonObject message = new JsonObject();
      message.addProperty("role", "user");
      message.addProperty("content", prompt);

      JsonArray messages = new JsonArray();
      messages.add(message);

      JsonObject requestBody = new JsonObject();
      requestBody.addProperty("model", "gpt-3.5-turbo");
      requestBody.add("messages", messages);

      okhttp3.Request apiRequest =
          new okhttp3.Request.Builder()
              .url(API_URL)
              .addHeader("Authorization", "Bearer " + API_KEY)
              .addHeader("Content-Type", "application/json")
              .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
              .build();

      try (okhttp3.Response apiResponse = client.newCall(apiRequest).execute()) {
        if (!apiResponse.isSuccessful()) {
          String errorMessage =
              "API error: " + apiResponse.code() + " - " + apiResponse.body().string();
          System.out.println(errorMessage); // Log the API response error
          responseMap.put("result", "error");
          responseMap.put("error", errorMessage);
          break;
        } else {
          String responseBody = apiResponse.body().string();

          JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
          String content =
              json.getAsJsonArray("choices")
                  .get(0)
                  .getAsJsonObject()
                  .getAsJsonObject("message")
                  .get("content")
                  .getAsString();

          // Extract the trivia question, options, and answer from the API response content
          String question = extractField(content, "question");
          if (seenQuestions.contains(question)) {
            continue;
          }
          String options = extractField(content, "options");
          String answer = extractField(content, "answer");

          String[] choices =
              options
                  .substring(1, options.length() - 1)
                  .split(", "); // This is removed, as it's handled now
          Map<String, Object> questionMap = new HashMap<>();

          questionMap.put("question", question);
          questionMap.put("options", choices); // `options` now contains the cleaned list of options
          questionMap.put("answer", answer);
          questionsList.add(questionMap);
        }

      } catch (IOException e) {
        e.printStackTrace(); // Print stack trace for debugging
        responseMap.put("result", "error");
        responseMap.put("error", "IOException: " + e.getMessage());
      }
    }

    // Serialize the Map into JSON response
    responseMap.put("questions", questionsList);
    return adapter.toJson(responseMap);
  }

  private String extractField(String content, String field) {
    try {
      JsonObject contentJson = JsonParser.parseString(content).getAsJsonObject();

      if (field.equals("question")) {
        return contentJson.get("question").getAsString();
      } else if (field.equals("options")) {
        JsonArray optionsArray = contentJson.getAsJsonArray("options");
        // Convert JsonArray to a plain Java array or list
        List<String> optionsList = new ArrayList<>();
        for (int i = 0; i < optionsArray.size(); i++) {
          optionsList.add(optionsArray.get(i).getAsString()); // Remove quotes while adding
        }
        return optionsList.toString(); // Return the cleaned list as a string
      } else if (field.equals("answer")) {
        return contentJson.get("answer").getAsString();
      }
    } catch (Exception e) {
      System.out.println("Error extracting field: " + e.getMessage());
      return "";
    }
    return "";
  }

  public JsonArray extractOptions(String content, String field) {
    try {
      JsonObject contentJson = JsonParser.parseString(content).getAsJsonObject();
      return contentJson.get("options").getAsJsonArray();
    } catch (Exception e) {
      System.out.println("Error extracting options: " + e.getMessage());
      return new JsonArray(); // Return an empty array if extraction fails
    }
  }

  public static String getDifficultyLevel(Map<Number, String> difficultyLevel, Number value) {
    return difficultyLevel.entrySet().stream()
        .sorted(Comparator.comparingDouble(entry -> entry.getKey().doubleValue()))
        .filter(entry -> value.doubleValue() < entry.getKey().doubleValue())
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse("Expert"); // default if value is above all defined levels
  }
}
