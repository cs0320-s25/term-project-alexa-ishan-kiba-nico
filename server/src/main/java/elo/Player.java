package elo;

public class Player {
  private String id;
  private String name;
  private int rating;

  public Player(String id, String name) {
    this.id = id;
    this.name = name;
    this.rating = 1200; // start everyone at 1200
  }

  /**
   * Returns a defensive copy of a user's rating
   *
   * @return an integer which is a defensive copy of the user's rating
   */
  public int getRating() {
    int ratingCopy = this.rating;
    return ratingCopy;
  }

  /**
   * Updates a user's rating as per the formula for the Elo rating
   *
   * @param opponentRating: a user's opponent's rating. used for probability calculation
   * @param score: a user's score based on a win/loss in the match
   * @param k: a constant by which ratings are multiplied; can be modified based on our desires for
   *     rating scale
   */
  public void updateRating(int opponentRating, double score, int k) {
    double expectedScore = 1.0 / (1 + Math.pow(10, (opponentRating - this.rating) / 400.0));
    this.rating += (int) (k * (score - expectedScore));
  }
}
