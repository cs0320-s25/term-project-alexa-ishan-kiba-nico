package ranker;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import storage.RankedUser;
import storage.StorageInterface;
import storage.User;

public class Ranker {

  private StorageInterface storageHandler;

  public Ranker(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  public List<User> sortUsers() throws Exception {
    List<User> users = storageHandler.getAllUsers();

    users.sort((a, b) -> Integer.compare(b.getElo(), a.getElo()));

    return users;
  }

  public List<RankedUser> rankUsers() throws Exception {
    List<User> sorted = sortUsers();
    List<RankedUser> rankedUsers = new ArrayList<RankedUser>();
    int currentRank = 1;
    int displayedRank = 1;
    int previousElo = 0;

    for (int i = 0; i < sorted.size(); i++) {
      User user = sorted.get(i);

      if (i > 0 && user.getElo() < previousElo) {
        displayedRank = currentRank;
      }

      rankedUsers.add(new RankedUser(displayedRank, user.getUsername(), user.getElo()));
      currentRank++;
      previousElo = user.getElo();
    }

    return rankedUsers;
  }

  public List<RankedUser> getLeaderboard(String username) throws Exception {
    List<RankedUser> rankedUsers = rankUsers();

    int cutoff = 10;
    List<RankedUser> leaderboard =
        rankedUsers.stream().filter(user -> user.getRank() <= cutoff).collect(Collectors.toList());

    Optional<RankedUser> currentUser =
        rankedUsers.stream().filter(user -> user.getUsername().equals(username)).findFirst();

    if (currentUser.isPresent() && currentUser.get().getRank() > cutoff) {
      leaderboard.add(currentUser.get());
    }
    ;

    return leaderboard;
  }
}
