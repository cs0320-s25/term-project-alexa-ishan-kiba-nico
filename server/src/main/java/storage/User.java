package storage;

public class User {
  private String username;
  private int elo;
  private String date;
  private Boolean played;

  public User() {}

  public User(String username, int elo, String date, Boolean played) {
    this.username = username;
    this.elo = elo;
    this.date = date;
    this.played = played;
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

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public Boolean getPlayed() {
    return played;
  }

  public void setPlayed(Boolean played) {
    this.played = played;
  }
}
