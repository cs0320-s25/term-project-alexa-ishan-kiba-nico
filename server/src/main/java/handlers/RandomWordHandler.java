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

/**
 * RandomWordHandler generates and returns a new random word for use in a trivia game.
 *
 * <p>It uses the OpenAI API to generate a word if one has not already been generated today. The
 * word is stored with its associated date using the provided StorageInterface, ensuring the same
 * word is returned throughout the day.
 */
public class RandomWordHandler implements Route {
  private static final String API_KEY = System.getenv("OPEN_API_KEY");
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private final StorageInterface storageInterface;

  public RandomWordHandler(StorageInterface storageInterface) {
    this.storageInterface = storageInterface;
  }

  /**
   * Handles incoming GET requests and returns a random word.
   *
   * <p>- If a word hasn't been generated today, it uses the OpenAI API to create one. - Otherwise,
   * it returns the stored word from earlier in the day.
   *
   * <p>The word is stored along with the current date to ensure daily consistency.
   *
   * <p>/** Invoked when a request is made on this route's corresponding path e.g. '/hello'
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

    // check if word was already generated for the day
    if (!storageInterface.getDailyWord().get("date").equals(LocalDate.now().toString())) {

      // create the prompt for open ai api
      String prompt =
          "Please generate me a random word that I can use for a trivia game. Make "
              + "your response to just be the word";

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
              .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
              .build();

      // process the response

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

        // error checking
      } catch (Exception e) {
        responseMap.put("result", "error");
        responseMap.put("message", e.getMessage());
      }
    } else {

      // if date in storage interface matches with current date, use the word for that date
      responseMap.put("result", "success");
      responseMap.put("word", storageInterface.getDailyWord().get("word"));
    }

    // return a serialized response map

    return adapter.toJson(responseMap);
  }
}
