package ranker;

import java.util.List;
import storage.StorageInterface;
import storage.User;

public class Ranker {

  private StorageInterface storageHandler;

  public Ranker(StorageInterface storageHandler) {
    this.storageHandler = storageHandler;
  }

  public List<User> rank(List<User> words) throws Exception {
    List<User> users = storageHandler.getAllUsers();

    }
  }
}
