import java.util.ArrayList;
import java.util.List;
import storage.User;

public class MockData {
  public static List<User> getMockedUsers() {
    List<User> users = new ArrayList<>();

    users.add(new User("Alice", 1500)); //8
    users.add(new User("Bob", 1800)); //2
    users.add(new User("Charlie", 1750)); //4
    users.add(new User("Daisy", 1800)); //2
    users.add(new User("Eve", 1600)); //10
    users.add(new User("Frank", 1400)); //11
    users.add(new User("Grace", 2000)); //1
    users.add(new User("Heidi", 1550)); //7
    users.add(new User("Ivan", 1700)); //5
    users.add(new User("Judy", 1650)); //6
    users.add(new User("Ken", 1500)); //8
    users.add(new User("Lisa", 100)); //12

    return users;
  }
}
