package main;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import user.User;

public class JDBC extends UnicastRemoteObject implements JDBCInterface {

  /**
   * 
   */
  private static final long serialVersionUID = 5620182200054035001L;
  // Cache
  private HashMap<Integer, User> users = new HashMap<>();

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private String DB_URL;

  // Database credentials
  private String USER;
  private String PASS;

  public JDBC(String DB_URL, String USER, String PASS) throws RemoteException {
    super(0);
    this.DB_URL = "jdbc:mysql://" + DB_URL;
    this.USER = USER;
    this.PASS = PASS;
    createDatabase();
    createTable();
  }

  private void createDatabase() {
    Connection conn = null;
    Statement stmt = null;
    try {
      // STEP 2: Register JDBC driver
      Class.forName(JDBC_DRIVER);

      // STEP 3: Open a connection
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      // STEP 4: Execute a query
      System.out.println("Creating database...");
      stmt = conn.createStatement();

      String sql = "CREATE DATABASE IF NOT EXISTS DS";
      stmt.executeUpdate(sql);
      System.out.println("Database created successfully...");
      this.DB_URL += "/DS";
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
          stmt.close();
      } catch (SQLException se2) {
      } // nothing we can do
      try {
        if (conn != null)
          conn.close();
      } catch (SQLException se) {
        se.printStackTrace();
      } // end finally try
    } // end try
    System.out.println("Goodbye!");
  }

  private void createTable() {
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
      System.out.println("Creating table in given database...");
      stmt = conn.createStatement();

      String sql =
          "CREATE TABLE IF NOT EXISTS `DS`.`Users` (`id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, `nome` text NOT NULL, `localidade` text NOT NULL, `data` date NOT NULL, PRIMARY KEY(`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;";

      stmt.executeUpdate(sql);
      System.out.println("Created table in given database...");
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
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONObject insertUser(User user) throws RemoteException {
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

      String sql = "INSERT INTO Users (nome, localidade, data)" + " VALUES ('" + user.getNome()
          + "', '" + user.getLocalidade() + "', '" + user.getData_nascimento().toString() + "')";
      stmt.executeUpdate(sql);
      System.out.println("Inserted records into the table...");

      sql = "SELECT * FROM Users";
      ResultSet rs = stmt.executeQuery(sql);

      if (rs.last()) {
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("nome", rs.getString("nome"));
        obj.put("localidade", rs.getString("localidade"));
        obj.put("datanascimento", rs.getDate("data").toString());
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
  public JSONObject deleteUser(Integer id) throws RemoteException {
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
      String sql = "SELECT * FROM Users WHERE id = " + id;
      ResultSet rs = stmt.executeQuery(sql);
      while (rs.next()) {
        count++;
      }
      rs.close();

      sql = "DELETE FROM Users " + "WHERE id = " + id;
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
    users.remove(id);
    obj.put("message", "Delete Successful.");
    return obj;
  }

  @SuppressWarnings("unchecked")
  @Override
  public JSONArray selectAllUsers() throws RemoteException {
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

      String sql = "SELECT * FROM Users";
      ResultSet rs = stmt.executeQuery(sql);

      // STEP 5: Extract data from result set
      while (rs.next()) {
        JSONObject obj = new JSONObject();
        // Retrieve by column name
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("nome", rs.getString("nome"));
        obj.put("localidade", rs.getString("localidade"));
        obj.put("datanascimento", rs.getDate("data").toString());
        array.add(obj);

        // Adds to cache
        users.put(rs.getInt("id"), new User(rs.getString("nome"), rs.getDate("data").toLocalDate(),
            rs.getString("localidade")));
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
  public JSONObject selectUser(Integer id) throws RemoteException {
    JSONObject obj = new JSONObject();
    User user = users.get(id);
    // Checks cache for user
    if (user != null) {
      obj.put("id", id);
      obj.put("nome", user.getNome());
      obj.put("localidade", user.getLocalidade());
      obj.put("datanascimento", user.getData_nascimento().toString());
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

        String sql = "SELECT * FROM Users WHERE id = " + id;
        ResultSet rs = stmt.executeQuery(sql);

        // STEP 5: Extract data from result set
        while (rs.next()) {
          // Retrieve by column name
          obj.put("id", String.valueOf(rs.getInt("id")));
          obj.put("nome", rs.getString("nome"));
          obj.put("localidade", rs.getString("localidade"));
          obj.put("datanascimento", rs.getDate("data").toString());
          count++;

          // Adds to cache
          users.put(rs.getInt("id"), new User(rs.getString("nome"),
              rs.getDate("data").toLocalDate(), rs.getString("localidade")));
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
  public JSONObject updateUser(Integer id, User user) throws RemoteException {
    Connection conn = null;
    Statement stmt = null;
    JSONObject obj = new JSONObject();
    Integer count = 0;
    try {
      // Open a connection
      System.out.println("Connecting to a selected database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);
      System.out.println("Connected database successfully...");

      // Execute a query
      System.out.println("Creating statement...");
      stmt = conn.createStatement();
      String sql = "UPDATE Users " + "SET nome = '" + user.getNome() + "', localidade = '"
          + user.getLocalidade() + "', data = '" + user.getData_nascimento().toString()
          + "' WHERE id = " + id;
      stmt.executeUpdate(sql);

      // Now you can extract all the records
      // to see the updated records
      sql = "SELECT * FROM Users WHERE id = " + id;
      ResultSet rs = stmt.executeQuery(sql);

      // Extract data from result set
      while (rs.next()) {
        // Retrieve by column name
        obj.put("id", String.valueOf(rs.getInt("id")));
        obj.put("nome", rs.getString("nome"));
        obj.put("localidade", rs.getString("localidade"));
        obj.put("datanascimento", rs.getDate("data").toString());
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
    users.remove(id);
    return obj;
  }
}
