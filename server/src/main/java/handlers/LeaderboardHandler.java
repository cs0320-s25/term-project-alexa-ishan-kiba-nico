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

public class LeaderboardHandler implements Route {

  public StorageInterface storageHandler;

  public LeaderboardHandler(StorageInterface storageHandler) {this.storageHandler = storageHandler;}

  @Override
  public Object handle(Request request, Response response) {
    String category = request.queryParams("uid");
    Moshi moshi = new Moshi.Builder().build();
    Type mapStringObject = Types.newParameterizedType(Map.class, String.class, Object.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();


    return adapter.toJson(responseMap);
  }
}
