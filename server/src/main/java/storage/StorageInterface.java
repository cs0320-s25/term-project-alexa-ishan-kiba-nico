package storage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public interface StorageInterface {

  void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data);

  void addData(String uid, Map<String, Object> data);

  List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException;

  List<Map<String, Object>> getGlobalCollection(String collection_id) throws Exception;

  List<String> getAllUserIds() throws InterruptedException, ExecutionException;

  List<User> getAllUsers() throws InterruptedException, ExecutionException;

  Map<String, Object> getData(String uid) throws InterruptedException, ExecutionException;

  void clearUser(String uid) throws InterruptedException, ExecutionException;
}
