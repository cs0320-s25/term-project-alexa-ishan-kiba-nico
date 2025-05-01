package storage;

public class User {
  private String username;
  private int elo;

  public User() {}

  public User(String username, int elo) {
    this.username = username;
    this.elo = elo;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getElo() {
    return elo;
  }

  public void setElo(int elo) {
    this.elo = elo;
  }
}
