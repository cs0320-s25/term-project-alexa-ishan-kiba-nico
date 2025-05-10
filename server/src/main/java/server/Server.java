package server;

import static spark.Spark.after;

import handlers.DailyLeaderboardHandler;
import handlers.DailyTriviaHandler;
import handlers.PlayedHandler;
import handlers.PointsHandler;
import handlers.TopicHandler;
import handlers.TopicLeaderboardHandler;
import handlers.TriviaQuestionHandler;
import handlers.UserHandler;
import java.io.IOException;
import spark.Spark;
import storage.FirebaseUtilities;
import storage.StorageInterface;

/** Class to initialize the server that starts Spark and runs various handlers. */
public class Server {

  static final int port = 3232;

  /** Constructor to build a server that starts Spark and runs various handlers. */
  public Server() {
    StorageInterface firebaseUtils;
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    try {
      firebaseUtils = new FirebaseUtilities();

      Spark.get("/dailyleaderboard", new DailyLeaderboardHandler(firebaseUtils));
      Spark.get("/topicleaderboard", new TopicLeaderboardHandler(firebaseUtils));
      Spark.get("/user", new UserHandler(firebaseUtils));
      Spark.get("/points", new PointsHandler(firebaseUtils));
      Spark.get("/question", new TriviaQuestionHandler(firebaseUtils));
      Spark.get("/daily", new DailyTriviaHandler());
      Spark.get("/played", new PlayedHandler(firebaseUtils));
      Spark.get("/topic", new TopicHandler(firebaseUtils));

      Spark.init();
      Spark.awaitInitialization();

      // Server started at http://localhost:3232)
      System.out.println("Server started");
    } catch (IOException e) {
      System.err.println(
          "Error: Could not initialize Firebase. Likely due to firebase_config.json not being found. Exiting.");
      System.exit(1);
    }
  }

  /**
   * Method to start the server.
   *
   * @param args - get requests to be processed by the server
   */
  public static void main(String[] args) {
    new Server();
  }
}
