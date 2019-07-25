package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import org.json.simple.JSONObject;

public class Select implements Callable<Object> {
  // Attributes
  private String query;
  private Type type;

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private String DB_URL;

  // Database credentials
  private String USER;
  private String PASS;

  public Select(String query, Type type, String DB_URL, String USER, String PASS) {
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
        return selectMessage(query);
      default:
        return selectUser(query);
    }
  }

  @SuppressWarnings("unchecked")
  public JSONObject selectUser(String query) throws SQLException, ClassNotFoundException {
    JSONObject object = new JSONObject();

    // Register JDBC driver
    Class.forName(JDBC_DRIVER);

    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    System.out.println("Connected database successfully...");

    // Execute a query
    System.out.println("Creating statement...");
    PreparedStatement stmt = conn.prepareStatement(query);

    ResultSet rs = stmt.executeQuery();

    // Extract data from result set
    if (rs.next()) {
      // Retrieve by column name
      object.put("id", rs.getInt("id"));
      object.put("nome", rs.getString("nome"));
      object.put("datanascimento", rs.getDate("datanascimento").toString());
      object.put("localidade", rs.getString("localidade"));
    } else {
      object = null;
    }
    rs.close();

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");

    return object;
  }

  @SuppressWarnings("unchecked")
  public JSONObject selectMessage(String query) throws SQLException, ClassNotFoundException {
    JSONObject object = new JSONObject();

    // Register JDBC driver
    Class.forName(JDBC_DRIVER);

    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    System.out.println("Connected database successfully...");

    // Execute a query
    System.out.println("Creating statement...");
    PreparedStatement stmt = conn.prepareStatement(query);

    ResultSet rs = stmt.executeQuery();

    // Extract data from result set
    if (rs.next()) {
      // Retrieve by column name
      object.put("id", rs.getInt("id"));
      object.put("nome", rs.getString("nome"));
      object.put("data", rs.getTimestamp("date").toString());
    } else {
      object = null;
    }
    rs.close();

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");

    return object;
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
