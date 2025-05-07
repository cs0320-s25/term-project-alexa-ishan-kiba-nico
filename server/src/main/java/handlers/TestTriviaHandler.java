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

public class TestTriviaHandler implements Route {

  private static final String API_KEY = System.getenv("OPEN_API_KEY");
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";

  @Override
  public Object handle(Request request, Response response) {
    String topic = request.queryParams("topic");
    String elo = request.queryParams("elo");
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);

    if (elo == null || topic == null) {
      response.status(400);
      return "Missing required query parameters: 'elo' and/or 'topic'";
    }

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
            .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
            .build();

    try (okhttp3.Response apiResponse = client.newCall(apiRequest).execute()) {
      if (!apiResponse.isSuccessful()) {
        System.out.println(
            "API error: " + apiResponse.code() + " - " + apiResponse.body().string());
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

        JsonArray questionsArray = JsonParser.parseString(content).getAsJsonArray();
        List<Map<String, Object>> questions = new ArrayList<>();

        for (JsonElement elem : questionsArray) {
          Map<String, Object> questionMap = new HashMap<>();
          JsonObject questionObj = elem.getAsJsonObject();
          String question = questionObj.get("question").getAsString();

          JsonArray options = questionObj.getAsJsonArray("options");
          String answer = questionObj.get("answer").getAsString();

          List<String> choices = new ArrayList<>();

          for (JsonElement opt : options) {
            choices.add(opt.getAsString());
          }
          questionMap.put("question", question);
          questionMap.put("options", choices);
          questionMap.put("answer", answer);
          questions.add(questionMap);
        }
        responseMap.put("questions", questions);
        responseMap.put("result", "success");
        return adapter.toJson(responseMap);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "IOException occurred. See server logs.";
    }
  }
}
