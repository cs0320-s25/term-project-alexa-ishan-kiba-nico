package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ranker.Ranker;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.RankedUser;
import storage.StorageInterface;

/**
 * Handles the "/dailyleaderboard" endpoint; handles HTTP requests to generate and return the daily
 * leaderboard for a given username.
 */
public class DailyLeaderboardHandler implements Route {

  private final StorageInterface storageHandler;

  public DailyLeaderboardHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String username = request.queryParams("username");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (username == null || username.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "Username is required");
      return adapter.toJson(responseMap);
    }

    try {
      Ranker ranker = new Ranker(this.storageHandler);
      List<RankedUser> leaderboard = new ArrayList<>(ranker.getLeaderboard(username));
      responseMap.put("result", "success");
      responseMap.put("leaderboard", leaderboard);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }
    return adapter.toJson(responseMap);
  }
}
