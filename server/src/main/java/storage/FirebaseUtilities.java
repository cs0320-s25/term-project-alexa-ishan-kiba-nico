package storage;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionGroup;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FirebaseUtilities implements StorageInterface {

  public FirebaseUtilities() throws IOException {
    // TODO: FIRESTORE PART 0:
    // Create /resources/ folder with firebase_config.json and
    // add your admin SDK from Firebase. see:
    // https://docs.google.com/document/d/10HuDtBWjkUoCaVj_A53IFm5torB_ws06fW3KYFZqKjc/edit?usp=sharing
    String workingDirectory = System.getProperty("user.dir");
    Path firebaseConfigPath =
        Paths.get(workingDirectory, "src", "main", "resources", "firebase_config.json");
    // ^-- if your /resources/firebase_config.json exists but is not found,
    // try printing workingDirectory and messing around with this path.

    FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath.toString());

    FirebaseOptions options =
        new FirebaseOptions.Builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

    FirebaseApp.initializeApp(options);
  }

  @Override
  public List<Map<String, Object>> getCollection(String uid, String collection_id)
      throws InterruptedException, ExecutionException, IllegalArgumentException {
    if (uid == null || collection_id == null) {
      throw new IllegalArgumentException("getCollection: uid and/or collection_id cannot be null");
    }

    // gets all documents in the collection 'collection_id' for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  @Override
  public List<Map<String, Object>> getGlobalCollection(String collection_id) throws Exception {

    Firestore db = FirestoreClient.getFirestore();
    // 1: Make the data payload to add to your collection
    CollectionGroup dataRef = db.collectionGroup(collection_id);

    // 2: Get pin documents
    QuerySnapshot dataQuery = dataRef.get().get();

    // 3: Get data from document queries
    List<Map<String, Object>> data = new ArrayList<>();
    for (QueryDocumentSnapshot doc : dataQuery.getDocuments()) {
      data.add(doc.getData());
    }

    return data;
  }

  @Override
  public Map<String, Object> getData(String uid) throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef = db.collection("users").document(uid);

    return docRef.get().get().getData();
  }

  @Override
  public Map<String, Object> getCategoryData(String category)
      throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef = db.collection("topics").document(category);

    return docRef.get().get().getData();
  }

  @Override
  public Map<String, Object> getDailyWord() throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    DocumentReference docRef = db.collection("daily").document("word");

    return docRef.get().get().getData();
  }

  @Override
  public void addDocument(String uid, String collection_id, String doc_id, Map<String, Object> data)
      throws IllegalArgumentException {
    if (uid == null || collection_id == null || doc_id == null || data == null) {
      throw new IllegalArgumentException(
          "addDocument: uid, collection_id, doc_id, or data cannot be null");
    }
    // adds a new document 'doc_name' to colleciton 'collection_id' for user 'uid'
    // with data payload 'data'.

    // TODO: FIRESTORE PART 1:
    // use the guide below to implement this handler
    // - https://firebase.google.com/docs/firestore/quickstart#add_data

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    CollectionReference dataRef = db.collection("users").document(uid).collection(collection_id);

    // 2: Write data to the collection ref
    dataRef.document(doc_id).set(data);
  }

  @Override
  public void addData(String uid, Map<String, Object> data) throws IllegalArgumentException {
    if (uid == null || data == null) {
      throw new IllegalArgumentException("addDocument: uid or data cannot be null");
    }
    // adds new data for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    DocumentReference dataRef = db.collection("users").document(uid);

    // 2: Write data to the collection ref
    dataRef.set(data);
  }

  @Override
  public void addCategoryData(String category, Map<String, Object> data)
      throws IllegalArgumentException {
    if (category == null || data == null) {
      throw new IllegalArgumentException("addDocument: uid or data cannot be null");
    }
    // adds new data for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    DocumentReference dataRef = db.collection("topics").document(category);

    // 2: Write data to the collection ref
    dataRef.set(data);
  }

  @Override
  public void addDailyWord(Map<String, Object> data) throws IllegalArgumentException {
    if (data == null) {
      throw new IllegalArgumentException("addDocument: data cannot be null");
    }
    // adds new data for user 'uid'

    Firestore db = FirestoreClient.getFirestore();
    // 1: Get a ref to the collection that you created
    DocumentReference dataRef = db.collection("daily").document("word");

    // 2: Write data to the collection ref
    dataRef.set(data);
  }

  @Override
  public List<String> getAllUserIds() throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    List<String> userIds = new ArrayList<>();

    ApiFuture<QuerySnapshot> future = db.collection("users").get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    for (QueryDocumentSnapshot doc : documents) {
      userIds.add(doc.getId());
    }

    return userIds;
  }

  @Override
  public List<String> getAllCategories() throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    List<String> categories = new ArrayList<>();

    ApiFuture<QuerySnapshot> future = db.collection("topics").get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    for (QueryDocumentSnapshot doc : documents) {
      categories.add(doc.getId());
    }

    return categories;
  }

  @Override
  public List<User> getAllUsers() throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();

    List<User> users = new ArrayList<>();

    ApiFuture<QuerySnapshot> future = db.collection("users").get();
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    for (QueryDocumentSnapshot doc : documents) {
      User u = doc.toObject(User.class);
      if (u.getUsername() != null) {
        users.add(u);
      }
    }

    return users;
  }

  // clears the collections inside of a specific user.
  @Override
  public void clearUser(String uid) throws IllegalArgumentException {
    if (uid == null) {
      throw new IllegalArgumentException("removeUser: uid cannot be null");
    }
    try {
      // removes all data for user 'uid'
      Firestore db = FirestoreClient.getFirestore();
      // 1: Get a ref to the user document
      DocumentReference userDoc = db.collection("users").document(uid);
      // 2: Delete the user document
      deleteDocument(userDoc);
    } catch (Exception e) {
      System.err.println("Error removing user : " + uid);
      System.err.println(e.getMessage());
    }
  }

  private void deleteDocument(DocumentReference doc) {
    // for each subcollection, run deleteCollection()
    Iterable<CollectionReference> collections = doc.listCollections();
    for (CollectionReference collection : collections) {
      deleteCollection(collection);
    }
    // then delete the document
    doc.delete();
  }

  // recursively removes all the documents and collections inside a collection
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#collections
  private void deleteCollection(CollectionReference collection) {
    try {

      // get all documents in the collection
      ApiFuture<QuerySnapshot> future = collection.get();
      List<QueryDocumentSnapshot> documents = future.get().getDocuments();

      // delete each document
      for (QueryDocumentSnapshot doc : documents) {
        doc.getReference().delete();
      }

      // NOTE: the query to documents may be arbitrarily large. A more robust
      // solution would involve batching the collection.get() call.
    } catch (Exception e) {
      System.err.println("Error deleting collection : " + e.getMessage());
    }
  }
}
