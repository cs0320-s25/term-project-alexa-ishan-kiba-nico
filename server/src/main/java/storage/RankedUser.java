package storage;

/**
 * Stores info about a user on the leaderboard, including rank, username, and Elo rating. These can
 * be accessed with getter methods.
 */
public class RankedUser {
  private int rank;
  private String username;
  private int elo;

  public RankedUser(int rank, String username, int elo) {
    this.rank = rank;
    this.username = username;
    this.elo = elo;
  }

  public int getRank() {
    return rank;
  }

  public String getUsername() {
    return username;
  }

  public int getElo() {
    return elo;
  }
}
