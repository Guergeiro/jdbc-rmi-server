package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import databases.Database;
import databases.Delete;
import databases.Insert;
import databases.Select;
import databases.SelectAll;
import databases.Type;
import databases.Update;
import messages.Message;
import responses.ResponseObject;
import users.User;

public class JDBC extends UnicastRemoteObject implements JDBCInterface {

  /**
   * 
   */
  private static final long serialVersionUID = 5620182200054035001L;
  // Cache
  private HashMap<Integer, User> users = new HashMap<>();
  private HashMap<Integer, Message> messages = new HashMap<>();

  // Attributes
  private ArrayList<Database> DBS;

  public JDBC(ArrayList<Database> DBS) throws RemoteException {
    super(0);
    this.DBS = DBS;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject insertUser(User user) throws RemoteException {
    String query = "INSERT INTO Users (nome, localidade, data)" + " VALUES ('" + user.getNome()
        + "', '" + user.getLocalidade() + "', '" + user.getData_nascimento().toString() + "')";

    Insert insert = new Insert(query, randomDB());
    FutureTask<Object> task = new FutureTask<Object>(insert);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer id = null;
    try {
      id = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    obj.put("id", id);
    obj.put("nome", user.getNome());
    obj.put("localidade", user.getLocalidade());
    obj.put("datanascimento", user.getData_nascimento().toString());
    // Adds to cache
    users.put(id, user);

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject deleteUser(Integer id) throws RemoteException {
    String query = "DELETE FROM Users " + "WHERE id = " + id;
    Delete delete = new Delete(query, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(delete);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer rows = null;
    try {
      rows = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    if (rows == 0) {
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    obj.put("message", "Delete Successful.");

    // removes from cache
    users.remove(id);

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject selectAllUsers() throws RemoteException {
    String query = "SELECT * FROM Users";
    SelectAll select = new SelectAll(query, Type.USER, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(select);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    JSONArray array = null;
    try {
      array = (JSONArray) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    return new ResponseObject(200, array);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject selectUser(Integer id) throws RemoteException {
    User user = users.get(id);

    // Checks if user in cache
    if (user != null) {
      JSONObject obj = new JSONObject();
      obj.put("id", id);
      obj.put("nome", user.getNome());
      obj.put("localidade", user.getLocalidade());
      obj.put("datanascimento", user.getData_nascimento().toString());
      return new ResponseObject(200, obj);
    }

    String query = "SELECT * FROM Users WHERE id = " + id;
    Select select = new Select(query, Type.USER, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(select);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = null;
    try {
      obj = (JSONObject) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj = new JSONObject();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }
    if (obj == null) {
      obj = new JSONObject();
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    // Adds to cache
    users.put(id, new User((String) obj.get("nome"),
        LocalDate.parse((String) obj.get("datanascimento")), (String) obj.get("localidade")));

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject updateUser(Integer id, User user) throws RemoteException {
    String query = "UPDATE Users " + "SET nome = '" + user.getNome() + "', localidade = '"
        + user.getLocalidade() + "', data = '" + user.getData_nascimento().toString()
        + "' WHERE id = " + id;
    Update update = new Update(query, randomDB());
    FutureTask<Object> task = new FutureTask<Object>(update);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer row = null;
    try {
      row = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    if (row == 0) {
      obj = new JSONObject();
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    // Updates cache
    users.put(id, user);

    obj.put("id", id);
    obj.put("nome", user.getNome());
    obj.put("localidade", user.getLocalidade());
    obj.put("datanascimento", user.getData_nascimento().toString());
    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject insertMessage(Message message) throws RemoteException {
    String query = "INSERT INTO Users (nome, date)" + " VALUES ('" + message.getMessage() + "', '"
        + message.getDate().toString() + "')";

    Insert insert = new Insert(query, randomDB());
    FutureTask<Object> task = new FutureTask<Object>(insert);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer id = null;
    try {
      id = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    obj.put("id", id);
    obj.put("message", message.getMessage());
    // Adds to cache
    messages.put(id, message);

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject deleteMessage(Integer id) throws RemoteException {
    String query = "DELETE FROM Messages " + "WHERE id = " + id;
    Delete delete = new Delete(query, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(delete);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer rows = null;
    try {
      rows = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    if (rows == 0) {
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    obj.put("message", "Delete Successful.");

    // removes from cache
    messages.remove(id);

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject selectAllMessages() throws RemoteException {
    String query = "SELECT * FROM Messages";
    SelectAll select = new SelectAll(query, Type.MESSAGE, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(select);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    JSONArray array = null;
    try {
      array = (JSONArray) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    return new ResponseObject(200, array);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject selectMessage(Integer id) throws RemoteException {
    Message message = messages.get(id);

    // Checks if user in cache
    if (message != null) {
      JSONObject obj = new JSONObject();
      obj.put("id", id);
      obj.put("message", message.getMessage());
      obj.put("date", message.getDate().toString());
      return new ResponseObject(200, obj);
    }

    String query = "SELECT * FROM Messages WHERE id = " + id;
    Select select = new Select(query, Type.MESSAGE, randomDB());

    FutureTask<Object> task = new FutureTask<Object>(select);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = null;
    try {
      obj = (JSONObject) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj = new JSONObject();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }
    if (obj == null) {
      obj = new JSONObject();
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    // Adds to cache
    messages.put(id,
        new Message((String) obj.get("message"), Timestamp.valueOf((String) obj.get("date"))));

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject updateMessage(Integer id, Message message) throws RemoteException {
    String query = "UPDATE Messages " + "SET message = '" + message.getMessage() + "', date = '"
        + message.getDate().toString() + "' WHERE id = " + id;
    Update update = new Update(query, randomDB());
    FutureTask<Object> task = new FutureTask<Object>(update);
    Thread t = new Thread(task);
    t.start();

    JSONObject obj = new JSONObject();
    Integer row = null;
    try {
      row = (Integer) task.get();
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return new ResponseObject(500, obj);
    }

    if (row == 0) {
      obj = new JSONObject();
      obj.put("message", "Key doesn't exist.");
      return new ResponseObject(404, obj);
    }

    // Updates cache
    messages.put(id, message);

    obj.put("id", id);
    obj.put("message", message.getMessage());
    obj.put("date", message.getDate().toString());
    return new ResponseObject(200, obj);
  }

  // Get random db
  private Database randomDB() {
    return DBS.get((int) (DBS.size() * Math.random()));
  }
}
