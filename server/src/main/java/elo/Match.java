package elo;

// Used in simulating a match being played: for updating the elo system as necessary
public class Match {

  public static void playMatch(Player p1, Player p2, int result) {
    int k = 32;

    double scoreP1 = result == 1 ? 1 : result == 0 ? 0 : 0.5;
    double scoreP2 = 1 - scoreP1;

    int rating1 = p1.getRating();
    int rating2 = p2.getRating();

    p1.updateRating(rating2, scoreP1, k);
    p2.updateRating(rating1, scoreP2, k);
  }
}
