import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import storage.StorageInterface;
import storage.User;

public class MockedStorage implements StorageInterface {

  @Override
  public void addDocument(String uid, String collection_id, String doc_id,
      Map<String, Object> data) {

  }

  @Override
  public void addData(String uid, Map<String, Object> data) {
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException {
    return List.of();
  }

  @Override
  public List<Map<String, Object>> getGlobalCollection(String collection_id) throws Exception {
    return List.of();
  }

  @Override
  public List<String> getAllUserIds() throws InterruptedException, ExecutionException {
    return List.of();
  }

  @Override
  public List<User> getAllUsers() throws InterruptedException, ExecutionException {
    return MockData.getMockedUsers();
  }

  @Override
  public Map<String, Object> getData(String uid) throws InterruptedException, ExecutionException {
    return Map.of();
  }

  @Override
  public void clearUser(String uid) throws InterruptedException, ExecutionException {

  }
}
