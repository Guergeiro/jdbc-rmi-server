package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import database.Delete;
import database.Insert;
import database.SelectAll;
import database.Type;
import database.Update;
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

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private String DB_URL;

  // Database credentials
  private String USER;
  private String PASS;

  public JDBC(String DB_URL, String USER, String PASS) throws RemoteException {
    super(0);
    this.DB_URL = "jdbc:mysql://" + DB_URL + "/DS";
    this.USER = USER;
    this.PASS = PASS;
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject insertUser(User user) throws RemoteException {
    String query = "INSERT INTO Users (nome, localidade, data)" + " VALUES ('" + user.getNome()
        + "', '" + user.getLocalidade() + "', '" + user.getData_nascimento().toString() + "')";

    Insert insert = new Insert(query, DB_URL, USER, PASS);
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
    Delete delete = new Delete(query, DB_URL, USER, PASS);

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
    SelectAll select = new SelectAll(query, Type.USER, DB_URL, USER, PASS);

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

    // Adds to cache
    for (int i = 0; i < array.size(); i++) {
      JSONObject user = (JSONObject) array.get(i);
      Integer id = (Integer) user.get("id");
      User u = users.get(id);
      if (u == null) {
        users.put(id, new User((String) user.get("nome"),
            LocalDate.parse((String) user.get("datanascimento")), (String) user.get("localidade")));
      }
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
    SelectAll select = new SelectAll(query, Type.USER, DB_URL, USER, PASS);

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

    return new ResponseObject(200, obj);
  }

  @SuppressWarnings("unchecked")
  @Override
  public ResponseObject updateUser(Integer id, User user) throws RemoteException {
    String query = "UPDATE Users " + "SET nome = '" + user.getNome() + "', localidade = '"
        + user.getLocalidade() + "', data = '" + user.getData_nascimento().toString()
        + "' WHERE id = " + id;
    Update update = new Update(query, DB_URL, USER, PASS);
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
  public JSONObject insertMessage(Message message) throws RemoteException {
    Connection conn = null;
    Statement stmt = null;
    JSONObject obj = new JSONObject();
    try {
      // STEP 2: Register JDBC driver
      Class.forName(JDBC_DRIVER);

      // STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // STEP 4: Execute a query
      System.out.println("Inserting records into the table...");
      stmt = conn.createStatement();

      String sql = "INSERT INTO Messages (message)" + " VALUES ('" + message.getMessage() + "')";
      stmt.executeUpdate(sql);
      System.out.println("Inserted records into the table...");

      sql = "SELECT * FROM Messages";
      ResultSet rs = stmt.executeQuery(sql);

      if (rs.last()) {
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("message", rs.getString("message"));
      }
      rs.close();

    } catch (Exception e) {
      // Handle errors for Class.forName
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return obj;
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          conn.close();
      } catch (SQLException se) {
      } // do nothing
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");
    return obj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject deleteMessage(Integer id) throws RemoteException {
    Connection conn = null;
    Statement stmt = null;
    Integer count = 0;
    JSONObject obj = new JSONObject();

    try {
      // STEP 2: Register JDBC driver
      Class.forName(JDBC_DRIVER);

      // STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql = "SELECT * FROM Messages WHERE id = " + id;
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        count++;
      }
      rs.close();

      sql = "DELETE FROM Messages " + "WHERE id = " + id;
      stmt.executeUpdate(sql);

    } catch (Exception e) {
      e.printStackTrace();
      obj.put("message", "Database Error.");
      return obj;
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          conn.close();
      } catch (SQLException se) {
      } // do nothing
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");

    if (count == 0)
      return null;

    // Removes from cache
    messages.remove(id);
    obj.put("message", "Delete Successful.");
    return obj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONArray selectAllMessages() throws RemoteException {
    JSONArray array = new JSONArray();
    Connection conn = null;
    Statement stmt = null;
    try {
      // STEP 2: Register JDBC driver
      Class.forName(JDBC_DRIVER);

      // STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = "SELECT * FROM Messages";
      ResultSet rs = stmt.executeQuery(sql);

      // STEP 5: Extract data from result set
      while (rs.next()) {
        JSONObject obj = new JSONObject();
        // Retrieve by column name
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("message", rs.getString("message"));
        array.add(obj);

        // Adds to cache
        messages.put(rs.getInt("id"),
            new Message(rs.getString("message"), rs.getTimestamp("date")));
      }
      rs.close();

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          conn.close();
      } catch (SQLException se) {
      } // do nothing
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");
    return array;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject selectMessage(Integer id) throws RemoteException {
    JSONObject obj = new JSONObject();
    Message message = messages.get(id);
    // Checks cache for user
    if (message != null) {
      obj.put("id", id);
      obj.put("message", message.getMessage());
      obj.put("date", message.getDate().toString());
      return obj;
    } else {
      Connection conn = null;
      Statement stmt = null;

      Integer count = 0;

      try {
        // STEP 2: Register JDBC driver
        Class.forName(JDBC_DRIVER);

        // STEP 3: Open a connection
        System.out.println("Connecting to a selected database...");
        conn = DriverManager.getConnection(DB_URL, USER, PASS);
        System.out.println("Connected database successfully...");

        // STEP 4: Execute a query
        System.out.println("Creating statement...");
        stmt = conn.createStatement();

        String sql = "SELECT * FROM Messages WHERE id = " + id;
        ResultSet rs = stmt.executeQuery(sql);

        // STEP 5: Extract data from result set
        while (rs.next()) {
          // Retrieve by column name
          obj.put("id", String.valueOf(rs.getInt("id")));
          obj.put("message", rs.getString("message"));
          obj.put("date", rs.getTimestamp("date").toString());
          count++;

          // Adds to cache
          messages.put(rs.getInt("id"),
              new Message(rs.getString("message"), rs.getTimestamp("date")));
        }
        rs.close();

      } catch (Exception e) {
        e.printStackTrace();
        obj.put("message", "Database Error.");
        return obj;
      } finally {
        // finally block used to close resources
        try {
          if (stmt != null)
            conn.close();
        } catch (SQLException se) {
        } // do nothing
        try {
          if (conn != null)
            conn.close();
        } catch (SQLException se) {
          se.printStackTrace();
        } // end finally try
      } // end try
      System.out.println("Goodbye!");
      if (count == 0)
        return null;
      return obj;
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject updateMessage(Integer id, Message message) throws RemoteException {
    Connection conn = null;
    Statement stmt = null;
    JSONObject obj = new JSONObject();
    Integer count = 0;
    try {
      // STEP 2: Register JDBC driver
      Class.forName(JDBC_DRIVER);

      // STEP 3: Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // STEP 4: Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();

      String sql = "UPDATE Messages " + "SET message = '" + message.getMessage() + "', date = '"
          + new Timestamp(System.currentTimeMillis()) + "' WHERE id = " + id;
      stmt.executeUpdate(sql);

      // Now you can extract all the records
      // to see the updated records
      sql = "SELECT * FROM Messages WHERE id = " + id;
      ResultSet rs = stmt.executeQuery(sql);

      // Extract data from result set
      while (rs.next()) {
        // Retrieve by column name
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("message", rs.getString("message"));
        obj.put("date", rs.getTimestamp("date").toString());
        count++;
      }
      rs.close();
    } catch (SQLException se) {
      // Handle errors for JDBC
      se.printStackTrace();
    } catch (Exception e) {
      // Handle errors for Class.forName
      e.printStackTrace();
    } finally {
      // finally block used to close resources
      try {
        if (stmt != null)
          conn.close();
      } catch (SQLException se) {
      } // do nothing
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");

    if (count == 0)
      return null;

    // Removes from cache
    messages.remove(id);
    return obj;
  }


}
