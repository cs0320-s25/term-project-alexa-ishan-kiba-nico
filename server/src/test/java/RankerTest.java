import org.junit.jupiter.api.Test;
import java.util.List;
import ranker.Ranker;
import storage.FirebaseUtilities;
import storage.RankedUser;
import storage.User;

import static org.junit.jupiter.api.Assertions.*;

public class RankerTest {

  @Test
  public void testRankUsers() throws Exception {
    Ranker ranker = new Ranker(new FirebaseUtilities()) {
      @Override
      public List<User> sortUsers() {
        return MockData.getMockedUsers().stream()
            .sorted((a, b) -> Integer.compare(b.getElo(), a.getElo()))
            .toList();
      }
    };

    List<RankedUser> ranked = ranker.rankUsers();

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

    assertEquals(11, ranked.size()); // total mocked users
  }
}
