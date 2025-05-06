import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ranker.Ranker;
import storage.RankedUser;

public class RankerTest {

  private Ranker ranker;

  @BeforeEach
  void setUp() throws Exception {
    this.ranker = new Ranker(new MockedStorage());
  }

  @Test
  public void testRankUsers() throws Exception {
    List<RankedUser> ranked = this.ranker.rankUsers();

    // Assertions
    assertEquals("Grace", ranked.get(0).username());
    assertEquals(2000, ranked.get(0).elo());
    assertEquals(1, ranked.get(0).rank());

    assertEquals("Bob", ranked.get(1).username());
    assertEquals("Daisy", ranked.get(2).username());
    assertEquals(2, ranked.get(1).rank());
    assertEquals(2, ranked.get(2).rank()); // tie check

    assertEquals("Charlie", ranked.get(3).username());
    assertEquals(4, ranked.get(3).rank()); // after tie, rank increases

    assertEquals(12, ranked.size()); // total mocked users
  }

  @Test
  public void testUserInTop10() throws Exception {
    String username = "Grace";

    List<RankedUser> leaderboard = this.ranker.getLeaderboard(username);

    long count = leaderboard.stream()
        .filter(u -> u.username().equals(username))
        .count();

    assertEquals(1, count, "Top-10 user should appear only once");
    assertEquals(10, leaderboard.size(), "Top-10 users");
    assertTrue(leaderboard.stream().allMatch(u -> u.rank() <= 10),
        "All users should have rank <= 10"
    );
  }

  @Test
  public void testUserNotInTop10() throws Exception {
    String username = "Lisa";

    List<RankedUser> leaderboard = this.ranker.getLeaderboard(username);

    long count = leaderboard.stream()
        .filter(u -> u.username().equals(username))
        .count();

    assertEquals(1, count, "User not in top 10 should be added once");
    assertEquals(username, leaderboard.get(leaderboard.size() - 1).username(),
        "User not in top 10 should appear at the end");
  }
}
