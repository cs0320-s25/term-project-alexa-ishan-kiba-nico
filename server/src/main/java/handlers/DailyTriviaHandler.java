package handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.*;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

/**
 * DailyTriviaHandler generates and serves daily trivia questions based on a specified topic.
 *
 * <p>If the requested topic is different from the previously stored topic, it fetches new questions
 * from OpenAI's Chat Completions API. Otherwise, it returns cached questions.
 */
public class DailyTriviaHandler implements Route {

  private static final String API_KEY = System.getenv("OPEN_API_KEY");
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private StorageInterface storageInterface;

  /**
   * Constructs a new DailyTriviaHandler with a storage interface for caching daily questions.
   *
   * @param storageInterface interface used to store and retrieve trivia questions
   */
  public DailyTriviaHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  @Override
  public Object handle(Request request, Response response) {
    try {
      String topic = request.queryParams("topic");

      if (topic == null) {
        response.status(400);
        return "Missing required query parameters: 'topic'";
      }

      Map<String, Object> dailyQuestions = storageInterface.getDailyQuestions();
      if (dailyQuestions == null) {
        dailyQuestions = new HashMap<>();
      }

      Moshi moshi = new Moshi.Builder().build();
      Type mapStringObject =
          Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
      JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

      Map<String, Object> responseMap = new HashMap<>();

      // check if the topic matches the daily word if it doesn't generate ten new questions for new
      // word
      if (dailyQuestions.get("word") == null
          || !dailyQuestions.get("word").toString().equals(topic)) {
        Map<String, Object> dailyQuestionsMap = new HashMap<>();

        String prompt =
            """
            Generate 10 unique trivia questions that increase in difficulty in the following JSON format:
            [
              {
                "question": "string",
                "options": ["A: Option", "B: Option", "C: Option", "D: Option"],
                "answer": "Option: Option"
              }
            ]

            Make each question based on the topic: """
                + topic
                + ".\n";

        // build the response

        OkHttpClient client = new OkHttpClient();

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
                .post(
                    RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        // process the response

        try (okhttp3.Response apiResponse = client.newCall(apiRequest).execute()) {
          if (!apiResponse.isSuccessful()) {
            System.out.println(
                "API error: " + apiResponse.code() + " - " + apiResponse.body().string());
            response.status(500);
            return "API Error";
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

            // parse response as a json array to separate questions

            JsonArray questionsArray = JsonParser.parseString(content).getAsJsonArray();
            List<Map<String, Object>> questions = new ArrayList<>();

            // build each question and put it into a question map

            for (JsonElement elem : questionsArray) {
              Map<String, Object> questionMap = new HashMap<>();
              JsonObject questionObj = elem.getAsJsonObject();
              String question = questionObj.get("question").getAsString();

              JsonArray options = questionObj.getAsJsonArray("options");
              String answer = questionObj.get("answer").getAsString();

              List<String> choices = new ArrayList<>();

              // convert the options for a question into a list of strings
              for (JsonElement opt : options) {
                choices.add(opt.getAsString());
              }

              // add all components to the question map
              questionMap.put("question", question);
              questionMap.put("options", choices);
              questionMap.put("answer", answer);

              // add questionMap to array of questions
              questions.add(questionMap);
            }
            responseMap.put("questions", questions);
            responseMap.put("result", "success");
            dailyQuestionsMap.put("word", topic);
            dailyQuestionsMap.put("questions", questions);

            storageInterface.addDailyQuestions(dailyQuestionsMap);
          }
        } catch (IOException e) {
          e.printStackTrace();
          response.status(500);
          return "IOException occurred. See server logs.";
        }
      } else {
        responseMap.put("result", "success");
        responseMap.put("questions", dailyQuestions.get("questions"));
      }

      return adapter.toJson(responseMap);

    } catch (Exception e) {
      e.printStackTrace();
      response.status(500);
      return "Internal Server Error: " + e.getMessage();
    }
  }
}
