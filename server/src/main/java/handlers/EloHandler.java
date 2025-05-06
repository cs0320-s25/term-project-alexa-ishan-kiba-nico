//package handlers;
//
//import java.util.HashMap;
//import java.util.Map;
//import spark.Request;
//import spark.Response;
//import spark.Route;
//import storage.StorageInterface;
//
//public class EloHandler implements Route {
//
//  private final StorageInterface storage;
//
//  public EloHandler(StorageInterface storage) {
//    this.storage = storage;
//  }
//
//  @Override
//  public Object handle(Request req, Response res) throws Exception {
//
//    // player usernames
//    String p1Name = req.queryParams("player1");
//    String p2Name = req.queryParams("player2");
//    String resultStr = req.queryParams("result");
//
//    if (p1Name == null) {
//      res.status(400);
//      return "ERROR: Missing player1 username parameter";
//    }
//
//    if (p2Name == null) {
//      res.status(400);
//      return "ERROR: Missing player2 username parameter";
//    }
//
//    if (resultStr == null) {
//      res.status(400);
//      return "ERROR: Missing result parameter";
//    }
//
//    double result; // 0 if user 1 wins, 1
//    try {
//      resultDoub = Double.parseDouble(resultStr);
//      if (result != 0 && resultDoub != 1 && resultDoub != 0.5) throw new NumberFormatException();
//    } catch (NumberFormatException e) {
//      res.status(400);
//      return "Result must be 0, 1, or 0.5.";
//    }
//
//    // get current elo ratings
//
//    int p1Rating = (int) storage.getData(p1Name).get("elo");
//    int p2Rating = (int) storage.getData(p2Name).get("elo");
//
//    // calculate expected game results, to converto to Elo ratings
//
//    double expected1 = 1.0 / (1 + Math.pow(10, (p2Rating - p1Rating) / 400.0));
//    double expected2 = 1.0 - expected1;
//
//    // as per game results, calculate new rating
//
//    int k = 32;
//    int newP1 = (int) (p1Rating + k * (result - expected1));
//    int newP2 = (int) (p2Rating + k * ((1 - result) - expected2));
//
//    // update ratings in Firebase as per results of the game
//
//    Map<String, Object> newP1Data = new HashMap<>();
//    newP1Data.put("elo", newP1);
//    storage.addData(p1Name, newP1Data);
//
//    Map<String, Object> newP2Data = new HashMap<>();
//    newP2Data.put("elo", newP2);
//    storage.addData(p2Name, newP2Data);
//
//    return String.format("Updated ratings - %s: %d, %s: %d", p1Name, newP1, p2Name, newP2);
//  }
//}
