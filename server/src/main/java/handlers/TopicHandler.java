package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class TopicHandler implements Route {

  private final StorageInterface storageHandler;

  public TopicHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  public static String toTitleCase(String input) {
    if (input == null || input.isEmpty()) return input;
    return Arrays.stream(input.split(" "))
        .map(
            word ->
                word.length() > 1
                    ? word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase()
                    : word.toUpperCase())
        .collect(Collectors.joining(" "));
  }

  @Override
  public Object handle(Request request, Response response) {
    String category = request.queryParams("category");
    String username = request.queryParams("username");
    String streak = request.queryParams("streak");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (category == null || category.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "Username is required");
      return adapter.toJson(responseMap);
    }

    if (username == null || username.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "Username is required");
      return adapter.toJson(responseMap);
    }

    try {
      List<String> categories = new ArrayList<>(this.storageHandler.getAllCategories());
      String categoryName = toTitleCase(category);
      Integer streakInt = Integer.parseInt(streak);
      if (categories.contains(categoryName)) {
        Map<String, Object> categoryData = this.storageHandler.getCategoryData(categoryName);
        Object currentStreakObj =
            this.storageHandler.getCategoryData(categoryName).get("streak");

        if (currentStreakObj instanceof Number) {
          int currentStreak = ((Number) currentStreakObj).intValue();

          if (currentStreak < streakInt) {
            categoryData.put("username", username);
            categoryData.put("streak", streakInt);
            this.storageHandler.addCategoryData(categoryName, categoryData);
          }

          if (currentStreak == Integer.parseInt(streak)) {
            String currentUser =
                this.storageHandler.getCategoryData(categoryName).get("username").toString();
            categoryData.put("username", currentUser + ", " + username);
            this.storageHandler.addCategoryData(categoryName, categoryData);
          }
        }

      } else {
        Map<String, Object> categoryData = new HashMap<>();
        categoryData.put("username", username);
        categoryData.put("streak", streakInt);
        this.storageHandler.addCategoryData(categoryName, categoryData);
      }

      responseMap.put("result", "success");
      responseMap.put("category", categoryName);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
