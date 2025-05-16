package storage;

/**
 * Holds data for game Users including username, elo rating, date, and whether or not they have
 * played the day's game. These values can be set or retrieved.
 */
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
    return new String(username); // defensive copy
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public int getElo() {
    int newElo = elo;
    return newElo; // defensive copy
  }

  public void setElo(int elo) {
    this.elo = elo;
  }

  public String getDate() {
    return new String(date); // defensive copy
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
