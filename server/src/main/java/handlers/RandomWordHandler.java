package handlers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class RandomWordHandler implements Route {
  private static final String API_KEY = System.getenv("OPEN_API_KEY");
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final StorageInterface storageInterface;

  public RandomWordHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  /**
   * Invoked when a request is made on this route's corresponding path e.g. '/hello'
   *
   * @param request The request object providing information about the HTTP request
   * @param response The response object providing functionality for modifying the response
   * @return The content to be set in the response
   * @throws Exception implementation can choose to throw exception
   */
  @Override
  public Object handle(Request request, Response response) throws Exception {
    Map<String, Object> responseMap = new HashMap<>();
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> dailyWordMap = new HashMap<>();
    if (!storageInterface.getDailyWord().get("date").equals(LocalDate.now().toString())) {

      String prompt =
          "Please generate me a random word that I can use for a trivia game. Make "
              + "your response to just be the word";

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
          responseMap.put("result", "success");
          responseMap.put("word", content);
          dailyWordMap.put("date", LocalDate.now().toString());
          dailyWordMap.put("word", content);
          storageInterface.addDailyWord(dailyWordMap);
        }

      } catch (Exception e) {
        responseMap.put("result", "error");
        responseMap.put("message", e.getMessage());
      }
    } else {
      responseMap.put("result", "success");
      responseMap.put("word", storageInterface.getDailyWord().get("word"));
    }

    return adapter.toJson(responseMap);
  }
}
