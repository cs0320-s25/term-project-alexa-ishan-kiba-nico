package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

/**
 * Handles "/score" endpoint; updates a user's score (Elo) in storage based on provided uid and
 * score query parameters
 */
public class ScoreHandler implements Route {

  private StorageInterface storageHandler;

  public ScoreHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String uid = request.queryParams("uid");
    String score = request.queryParams("score");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (uid == null || uid.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "uid is required");
      return adapter.toJson(responseMap);
    }

    if (score == null || score.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "score is required");
      return adapter.toJson(responseMap);
    }

    try {
      Integer scoreInt = Integer.parseInt(score);
      Map<String, Object> userData = this.storageHandler.getData(uid);
      userData.put("elo", scoreInt);
      this.storageHandler.addData(uid, userData);
      responseMap.put("result", "success");
      responseMap.put("uid", uid);
      responseMap.put("score", scoreInt);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
