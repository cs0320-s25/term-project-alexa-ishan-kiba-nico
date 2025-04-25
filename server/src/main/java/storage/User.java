package storage;

public class User {

  private String username;
  private int elo;

  public User(String username, int elo) {
    this.username = username;
    this.elo = elo;
  }

  public String getUsername() {
    return username;
  }

  public int getElo() {
    return elo;
  }
}
