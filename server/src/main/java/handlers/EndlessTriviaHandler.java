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
 * EndlessTriviaHandler is a Spark route that generates trivia questions using the OpenAI API. Given
 * a topic via a query parameter, it returns a JSON-formatted trivia question with options and an
 * answer. This class uses the GPT-3.5-turbo model to generate the trivia content.
 */
public class EndlessTriviaHandler implements Route {

  private static final String API_KEY = System.getenv("OPEN_API_KEY");
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private StorageInterface storageInterface;

  /**
   * Constructs an EndlessTriviaHandler with a given storage interface.
   *
   * @param storageInterface an implementation of StorageInterface, for possible future use (e.g.,
   *     tracking questions, answers).
   */
  public EndlessTriviaHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  /**
   * Handles incoming HTTP requests to generate a trivia question. Expects a "topic" query
   * parameter, calls the OpenAI API, and returns a JSON object with the question, options, and
   * answer.
   *
   * @param request the Spark request object
   * @param response the Spark response object
   * @return a JSON string containing the trivia question or an error message
   */
  @Override
  public Object handle(Request request, Response response) {

    // using moshi to serialize the response map
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    try {
      String topic = request.queryParams("topic");

      if (topic == null) {
        response.status(400);
        return "Missing required query parameters: 'topic'";
      }

      // prompts the open ai api for a well formatted trivia question
      String prompt =
          """
          Generate a challenging unique trivia question in the following JSON format:
          {
            "question": "string",
            "options": ["A: Option", "B: Option", "C: Option", "D: Option"],
            "answer": "Letter: Option"
          }

          Make the question based on the topic: """
              + topic
              + ".\n";

      // Building the api request
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

      // processing the api response
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

          JsonObject questionObj = JsonParser.parseString(content.trim()).getAsJsonObject();

          String question = questionObj.get("question").getAsString();
          JsonArray options = questionObj.getAsJsonArray("options");
          String answer = questionObj.get("answer").getAsString();

          // parsing the potential into an array of strings
          List<String> choices = new ArrayList<>();
          for (JsonElement opt : options) {
            choices.add(opt.getAsString());
          }

          // formatting the parts of a question into a response map
          Map<String, Object> questionMap = new HashMap<>();
          questionMap.put("question", question);
          questionMap.put("options", choices);
          questionMap.put("answer", answer);

          responseMap.put("question", questionMap);
          responseMap.put("result", "success");
        }

        // error handling
      } catch (IOException e) {
        e.printStackTrace();
        response.status(500);
        responseMap.put("result", "error");
        responseMap.put("message", e.getMessage());
      }

    } catch (Exception e) {
      e.printStackTrace();
      response.status(500);
      responseMap.put("result", "error");
      responseMap.put("message", e.getMessage());
    }

    // return serialized response map using moshi
    return adapter.toJson(responseMap);
  }
}
