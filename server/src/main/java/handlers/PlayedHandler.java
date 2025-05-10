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

public class PlayedHandler implements Route {

  private final StorageInterface storageHandler;

  public PlayedHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String uid = request.queryParams("uid");
    String played = request.queryParams("played");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (uid == null || uid.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "Username is required");
      return adapter.toJson(responseMap);
    }

    try {
      if (played == null || played.isEmpty()) {
        Map<String, Object> userData = this.storageHandler.getData(uid);
        Object playedObject = userData.get("played");
        if (playedObject instanceof Boolean) {
          Boolean playedBoolean = (Boolean) playedObject;
          if (playedBoolean) {
            responseMap.put("result", "true");
          } else {
            responseMap.put("result", "false");
          }
        }
      } else if (played.equals("true")) {
        Map<String, Object> userData = this.storageHandler.getData(uid);
        userData.put("played", true);
        this.storageHandler.addData(uid, userData);
        responseMap.put("result", "success");
      }

    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
