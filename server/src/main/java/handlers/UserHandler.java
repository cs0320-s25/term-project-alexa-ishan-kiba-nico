package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class UserHandler implements Route {

  private final StorageInterface storageHandler;

  public UserHandler(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  @Override
  public Object handle(Request request, Response response) {
    String uid = request.queryParams("uid");
    String username = request.queryParams("username");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (uid == null || uid.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "uid is required");
      return adapter.toJson(responseMap);
    }

    if (username == null || username.isEmpty()) {
      responseMap.put("result", "failure");
      responseMap.put("error", "username is required");
      return adapter.toJson(responseMap);
    }

    try {
      List<String> users = this.storageHandler.getAllUserIds();
      LocalDate date = LocalDate.now();
      String dateString = date.toString();

      Map<String, Object> userData;
      if (users.contains(uid)) {
        userData = this.storageHandler.getData(uid);
        userData.put("username", username);
        if (!userData.get("date").equals(dateString)) {
          userData.put("elo", 0);
          userData.put("date", dateString);
          userData.put("played", false);
        }
      } else {
        userData = new HashMap<>();
        userData.put("username", username);
        userData.put("elo", 0);
        userData.put("date", dateString);
        userData.put("played", false);
      }
      this.storageHandler.addData(uid, userData);
      responseMap.put("result", "success");
      responseMap.put("uid", uid);
      responseMap.put("user", username);
    } catch (Exception e) {
      e.printStackTrace();
      responseMap.put("result", "failure");
      responseMap.put("error", e.getMessage());
    }

    return adapter.toJson(responseMap);
  }
}
