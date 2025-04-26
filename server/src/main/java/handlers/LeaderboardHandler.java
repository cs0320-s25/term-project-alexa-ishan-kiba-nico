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

public class LeaderboardHandler implements Route {

  public StorageInterface storageHandler;

  public LeaderboardHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String username = request.queryParams("username");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    try {
      Ranker ranker = new Ranker(storageHandler);
      List<RankedUser> leaderboard = new ArrayList<RankedUser>(ranker.getLeaderboard(username));
      responseMap.put("result", "success");
      responseMap.put("leaderboard", leaderboard);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("response_type", "failure");
      responseMap.put("error", e.getMessage());
    }
    return adapter.toJson(responseMap);
  }
}
