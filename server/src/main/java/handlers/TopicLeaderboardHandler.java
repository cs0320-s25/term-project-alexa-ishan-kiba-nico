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
        Object questionsCorrectObj = categoryData.get("questions");
        if (userObj == null || questionsCorrectObj == null) {
          continue;
        }

        if (userObj instanceof String && questionsCorrectObj instanceof Number) {
          String user = userObj.toString();
          int questionsCorrect = ((Number) questionsCorrectObj).intValue();
          leaderboard.add(new Category(category, user, questionsCorrect));
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
