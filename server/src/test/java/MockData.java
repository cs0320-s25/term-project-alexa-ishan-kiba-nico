import java.util.List;
import java.util.ArrayList;
import storage.User;

public class MockData {
  public static List<User> getMockedUsers() {
    List<User> users = new ArrayList<>();

    users.add(new User("Alice", 1500));
    users.add(new User("Bob", 1800));
    users.add(new User("Charlie", 1750));
    users.add(new User("Daisy", 1800));   // Tie with Bob
    users.add(new User("Eve", 1600));
    users.add(new User("Frank", 1400));
    users.add(new User("Grace", 2000));   // Top
    users.add(new User("Heidi", 1550));
    users.add(new User("Ivan", 1700));
    users.add(new User("Judy", 1650));
    users.add(new User("Ken", 1500));     // Tie with Alice

    return users;
  }
}