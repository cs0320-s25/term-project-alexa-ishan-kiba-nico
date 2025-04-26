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

  //
  /**
   * @return
   */
  public int getRating() {
    int ratingCopy = this.rating;
    return ratingCopy;
  }

  public void updateRating(int opponentRating, double score, int k) {
    double expectedScore = 1.0 / (1 + Math.pow(10, (opponentRating - this.rating) / 400.0));
    this.rating += (int) (k * (score - expectedScore));
  }
}
