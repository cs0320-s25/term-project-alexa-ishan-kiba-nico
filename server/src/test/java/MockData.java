import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import storage.User;

public class MockData {
  public static List<User> getMockedUsers() {
    List<User> users = new ArrayList<>();

    users.add(new User("Alice", 1500, LocalDate.now()));   // 8
    users.add(new User("Bob", 1800, LocalDate.now()));     // 2
    users.add(new User("Charlie", 1750, LocalDate.now())); // 4
    users.add(new User("Daisy", 1800, LocalDate.now()));   // 2
    users.add(new User("Eve", 1600, LocalDate.now()));     // 10
    users.add(new User("Grace", 2000, LocalDate.now()));   // 1
    users.add(new User("Ivan", 1700, LocalDate.now()));    // 5
    users.add(new User("Judy", 1650, LocalDate.now()));    // 6
    users.add(new User("Ken", 1500, LocalDate.now()));     // 8
    users.add(new User("Lisa", 100, LocalDate.now()));     // 12
    users.add(new User("Mallory", 1450, LocalDate.now())); // 11
    users.add(new User("Oscar", 1550, LocalDate.now()));   // 7

// These 3 users won't be ranked today due to old dates
    users.add(new User("Frank", 1400, LocalDate.now().minusDays(5)));  // not ranked
    users.add(new User("Heidi", 1550, LocalDate.now().minusDays(2)));  // not ranked
    users.add(new User("Niaj", 1600, LocalDate.now().minusDays(1)));   // not ranked

    return users;
  }
}
