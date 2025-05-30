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
    assertEquals("Grace", ranked.get(0).getUsername());
    assertEquals(2000, ranked.get(0).getElo());
    assertEquals(1, ranked.get(0).getRank());

    assertEquals("Bob", ranked.get(1).getUsername());
    assertEquals("Daisy", ranked.get(2).getUsername());
    assertEquals(2, ranked.get(1).getRank());
    assertEquals(2, ranked.get(2).getRank()); // tie check

    assertEquals("Charlie", ranked.get(3).getUsername());
    assertEquals(4, ranked.get(3).getRank()); // after tie, rank increases

    assertEquals(12, ranked.size()); // total mocked users
  }

  @Test
  public void testUserInTop10() throws Exception {
    String username = "Grace";

    List<RankedUser> leaderboard = this.ranker.getLeaderboard(username);

    long count = leaderboard.stream().filter(u -> u.getUsername().equals(username)).count();

    assertEquals(1, count, "Top-10 user should appear only once");
    assertEquals(10, leaderboard.size(), "Top-10 users");
    assertTrue(
        leaderboard.stream().allMatch(u -> u.getRank() <= 10), "All users should have rank <= 10");
  }

  @Test
  public void testUserNotInTop10() throws Exception {
    String username = "Lisa";

    List<RankedUser> leaderboard = this.ranker.getLeaderboard(username);

    long count = leaderboard.stream().filter(u -> u.getUsername().equals(username)).count();

    assertEquals(1, count, "User not in top 10 should be added once");
    assertEquals(
        username,
        leaderboard.get(leaderboard.size() - 1).getUsername(),
        "User not in top 10 should appear at the end");
  }
}
