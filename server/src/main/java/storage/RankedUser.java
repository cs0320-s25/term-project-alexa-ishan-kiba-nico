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
    int newRank = rank;
    return newRank; // defensive copy
  }

  public String getUsername() {
    String newUsername = username;
    return newUsername; // defensive copy
  }

  public int getElo() {
    int newElo = elo;
    return newElo; // defensive copy
  }
}
