package server;

import static spark.Spark.after;

import handlers.LeaderBoardHandler;
import spark.Spark;

/**
 * Class to initialize the server that starts Spark and runs various handlers.
 */
public class Server {

  static final int port = 3232;

  /**
   * Constructor to build a server that starts Spark and runs various handlers.
   */
  public Server() {
    Spark.port(port);

    after(
        (request, response) -> {
          response.header("Access-Control-Allow-Origin", "*");
          response.header("Access-Control-Allow-Methods", "*");
        });

    Spark.get("/leaderboard", new LeaderBoardHandler());
    Spark.init();
    Spark.awaitInitialization();

    // Server started at http://localhost:3232)
    System.out.println("Server started");
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
