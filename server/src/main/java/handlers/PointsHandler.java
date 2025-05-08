package handlers;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Moshi.Builder;
import com.squareup.moshi.Types;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import spark.Request;
import spark.Response;
import spark.Route;
import storage.StorageInterface;

public class PointsHandler implements Route {

  private final StorageInterface storage;

  public PointsHandler(StorageInterface storage) {
    this.storage = storage;
  }

  @Override
  public Object handle(Request req, Response res) throws Exception {

    // player usernames
    String currentScore = req.queryParams("currentscore");
    String timeToAnswer = req.queryParams("time");

    Moshi moshi = new Builder().build();
    Type mapStringObject =
        Types.newParameterizedType(Map.class, String.class, Object.class, List.class);
    JsonAdapter<Map<String, Object>> adapter = moshi.adapter(mapStringObject);
    Map<String, Object> responseMap = new HashMap<>();

    if (currentScore == null) {
      responseMap.put("result", "failure");
      responseMap.put("error", "ERROR: Missing current score parameter");
    }

    int currentScoreVal = 0;
    try {
      currentScoreVal = Integer.parseInt(currentScore);
      if (currentScoreVal < 0) {
        responseMap.put("result", "failure");
        responseMap.put("error", "ERROR: current score must be greater than or equal to 0");
        return adapter.toJson(responseMap);
      }
    } catch (NumberFormatException e) {
      responseMap.put("result", "failure");
      responseMap.put("error", "ERROR: current score parameter must be a number");
      return adapter.toJson(responseMap);
    }

    if (timeToAnswer == null) {
      responseMap.put("result", "failure");
      responseMap.put("error", "ERROR: Missing time parameter");
      return adapter.toJson(responseMap);
    }

    final double maxTime = // time (in sec)
        10.00; // modify this to change the longest possible answer that will score points
    final int maxScore =
        1000; // currently, the most points you can score per question is 1000. Modify this int to
    // change that.

    try {
      double timeTaken = Double.parseDouble(timeToAnswer);
      if (timeTaken < 0 || timeTaken > maxTime) {
        responseMap.put("result", "failure");
        responseMap.put("error", "ERROR: time must be between 0 and " + (maxTime));
      } else {
        double rawScore = maxScore * (1 - (timeTaken / maxTime));
        int questionScore =
            (int) Math.max(0, Math.round(rawScore)); // rounds points to the nearest whole number.
        // points cannot be negative.
        int finalScore = questionScore + currentScoreVal;

        responseMap.put("result", "success");
        responseMap.put("score", finalScore);
      }
    } catch (NumberFormatException e) {
      responseMap.put("result", "failure");
      responseMap.put("error", "ERROR: time must be a number");
    }

    return adapter.toJson(responseMap);
  }
}
