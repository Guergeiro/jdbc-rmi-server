package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class Update implements Callable<Object> {
  // Attributes
  private String query;

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private String DB_URL;

  // Database credentials
  private String USER;
  private String PASS;

  public Update(String query, String DB_URL, String USER, String PASS) {
    this.query = query;
    this.DB_URL = DB_URL;
    this.USER = USER;
    this.PASS = PASS;
  }

  @Override
  public Object call() throws ClassNotFoundException, SQLException {
    return update(query);
  }

  public Integer update(String query) throws SQLException, ClassNotFoundException {
    Integer rows = 0;
    
    // Register JDBC driver
    Class.forName(JDBC_DRIVER);
    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
    System.out.println("Connected database successfully...");
    
    // Execute a query
    System.out.println("Updating records...");
    PreparedStatement stmt = conn.prepareStatement(query);
    rows = stmt.executeUpdate();

    System.out.println("Updated records...");

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");
    return rows;
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
