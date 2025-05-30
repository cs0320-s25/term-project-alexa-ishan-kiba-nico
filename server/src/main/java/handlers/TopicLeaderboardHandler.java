package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.Category;
import storage.StorageInterface;

/**
 * Handles "/topicleaderboard" endpoint; returns the top streak holder for each trivia category from
 * stored leaderboard data.
 */
public class TopicLeaderboardHandler implements Route {

  private final StorageInterface storageHandler;

  public TopicLeaderboardHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      List<String> categories = this.storageHandler.getAllCategories();

      List<Category> leaderboard = new ArrayList<>();

      for (String category : categories) {
        Map<String, Object> categoryData = this.storageHandler.getCategoryData(category);
        Object userObj = categoryData.get("username");
        Object streakObj = categoryData.get("streak");
        if (userObj == null || streakObj == null) {
          continue;
        }

        if (userObj instanceof String && streakObj instanceof Number) {
          String user = userObj.toString();
          int streak = ((Number) streakObj).intValue();
          leaderboard.add(new Category(category, user, streak));
        }

        responseMap.put("result", "success");
        responseMap.put("leaderboard", leaderboard);
      }
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
