package handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okhttp3.*;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class TriviaQuestionHandler implements Route {

  private static final String API_KEY =
      System.getenv("OPEN_AI_API_KEY"); // Replace with env var in production
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final StorageInterface storageInterface;

  public TriviaQuestionHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  @Override
  public Object handle(Request request, Response response)
      throws ExecutionException, InterruptedException {
    //    String uid = request.queryParams("uid");
    String elo = request.queryParams("elo");
    String topic = request.queryParams("topic");

    if (elo == null || topic == null) {
      response.status(400);
      return "Missing required query parameters: 'elo' and/or 'topic'";
    }

    Number eloRanking;
    try {
      eloRanking = Double.parseDouble(elo);
    } catch (NumberFormatException e) {
      response.status(400);
      return "Elo must be a valid number";
    }

    //    Number elo = (int) storageInterface.getData(uid).get("elo");
    Map<Number, String> difficultyLevel = new HashMap<>();
    difficultyLevel.put(20, "Easy");
    difficultyLevel.put(50, "Medium");
    difficultyLevel.put(100, "Hard");
    difficultyLevel.put(200, "Very Hard");
    difficultyLevel.put(500, "Expert");

    String level = getDifficultyLevel(difficultyLevel, eloRanking);

    OkHttpClient client = new OkHttpClient();
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();
    String prompt =
        """
      Generate one trivia question in JSON format with these fields:
      {
        "question": "string",
        "options": ["A: Option", "B: Option", "C: Option", "D: Option"],
        "answer": "Correct option from above"
      } """
            + "make the question of difficulty + "
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
        responseMap.put("result", "error");
        responseMap.put(
            "error", "API error: " + apiResponse.code() + " - " + apiResponse.body().string());
      } else {
        JsonObject json = JsonParser.parseString(apiResponse.body().string()).getAsJsonObject();
        String content =
            json.getAsJsonArray("choices")
                .get(0)
                .getAsJsonObject()
                .getAsJsonObject("message")
                .get("content")
                .getAsString();

        responseMap.put("result", "success");
        responseMap.put("response", content.trim());
      }
    } catch (IOException e) {
      responseMap.put("result", "error");
      responseMap.put("error", e.getMessage());
    }

    // Serialize the Map into JSON using Moshi
    return adapter.toJson(responseMap);
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
