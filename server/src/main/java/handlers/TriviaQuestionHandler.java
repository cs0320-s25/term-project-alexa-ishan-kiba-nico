package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import okhttp3.*;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class TriviaQuestionHandler implements Route {

  private static final String OPENAI_API_KEY = "YOUR_API_KEY"; // NICO YOU NEED API KEY REMEMBER TO PURCHASE FOR PROJECT
  private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    String prompt = """
        Generate one trivia question in JSON format with these fields:
        {
          "question": "string",
          "options": ["A", "B", "C", "D"],
          "answer": "Correct option from above"
        }
        Return only the JSON object.
        """;

    OkHttpClient client = new OkHttpClient();

    MediaType mediaType = MediaType.parse("application/json");
    String jsonPayload = String.format("""
        {
          "model": "gpt-4",
          "messages": [
            {"role": "user", "content": "%s"}
          ],
          "temperature": 0.7
        }
        """, prompt.replace("\"", "\\\""));

    RequestBody body = RequestBody.create(jsonPayload, mediaType);

    okhttp3.Request httpRequest = new okhttp3.Request.Builder()
        .url(OPENAI_URL)
        .post(body)
        .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
        .addHeader("Content-Type", "application/json")
        .build();

    try (okhttp3.Response apiResponse = client.newCall(httpRequest).execute()) {
      if (!apiResponse.isSuccessful()) {
        responseMap.put("result", "failure");
        responseMap.put("error", "Failed to call OpenAI API");
        return adapter.toJson(responseMap);
      }

      // Parse the JSON response from OpenAI
      String responseBody = apiResponse.body().toString();
      Map<String, Object> parsed = moshi.adapter(Map.class).fromJson(responseBody);

      // Extract the content of the message
      Map choice = ((java.util.List<Map>) parsed.get("choices")).get(0);
      Map message = (Map) choice.get("message");
      String content = (String) message.get("content");

      responseMap.put("result", "success");
      responseMap.put("trivia", content);

    } catch (IOException e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
