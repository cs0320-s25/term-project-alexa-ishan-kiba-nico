package storage;

import java.time.LocalDate;

public class User {
  private String username;
  private int elo;
  private LocalDate date;

  public User() {}

  public User(String username, int elo, LocalDate date) {
    this.username = username;
    this.elo = elo;
    this.date = date;
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {this.date = date;}
}
