package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SelectAll implements Callable<Object> {
  // Attributes
  private String query;
  private Type type;

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private String DB_URL;

  // Database credentials
  private String USER;
  private String PASS;

  public SelectAll(String query, Type type, String DB_URL, String USER, String PASS) {
    this.query = query;
    this.type = type;
    this.DB_URL = DB_URL;
    this.USER = USER;
    this.PASS = PASS;
  }

  @Override
  public Object call() throws ClassNotFoundException, SQLException {
    switch (this.type) {
      case MESSAGE:
        return selectMessages(query);
      default:
        return selectUsers(query);
    }
  }

  @SuppressWarnings("unchecked")
  public JSONArray selectUsers(String query) throws SQLException, ClassNotFoundException {
    JSONArray array = new JSONArray();

    // Register JDBC driver
    Class.forName(JDBC_DRIVER);

    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    System.out.println("Connected database successfully...");

    // STEP 4: Execute a query
    System.out.println("Creating statement...");
    PreparedStatement stmt = conn.prepareStatement(query);

    ResultSet rs = stmt.executeQuery();

    // STEP 5: Extract data from result set
    while (rs.next()) {
      JSONObject obj = new JSONObject();
      // Retrieve by column name
      obj.put("id", rs.getInt("id"));
      obj.put("nome", rs.getString("nome"));
      obj.put("datanascimento", rs.getDate("data").toString());
      obj.put("localidade", rs.getString("localidade"));
      array.add(obj);
    }
    rs.close();

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");

    return array;
  }

  @SuppressWarnings("unchecked")
  public JSONArray selectMessages(String query) throws SQLException, ClassNotFoundException {
    JSONArray array = new JSONArray();

    // Register JDBC driver
    Class.forName(JDBC_DRIVER);

    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    System.out.println("Connected database successfully...");

    // STEP 4: Execute a query
    System.out.println("Creating statement...");
    PreparedStatement stmt = conn.prepareStatement(query);

    ResultSet rs = stmt.executeQuery();

    // STEP 5: Extract data from result set
    while (rs.next()) {
      JSONObject obj = new JSONObject();
      // Retrieve by column name
      obj.put("id", rs.getInt("id"));
      obj.put("message", rs.getString("message"));
      obj.put("date", rs.getTimestamp("date").toString());
      array.add(obj);
    }
    rs.close();

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");

    return array;
  }

  // Get & Set
  public String getQuery() {
    return query;
  }

  public void setQuery(String query) {
    this.query = query;
  }

  public String getDB_URL() {
    return DB_URL;
  }

  public void setDB_URL(String dB_URL) {
    DB_URL = dB_URL;
  }

  public String getUSER() {
    return USER;
  }

  public void setUSER(String uSER) {
    USER = uSER;
  }

  public String getPASS() {
    return PASS;
  }

  public void setPASS(String pASS) {
    PASS = pASS;
  }

  public String getJDBC_DRIVER() {
    return JDBC_DRIVER;
  }
}
