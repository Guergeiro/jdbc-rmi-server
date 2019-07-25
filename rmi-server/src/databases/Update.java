package databases;

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
  private Database DB;

  public Update(String query, Database DB) {
    this.query = query;
    this.DB = DB;
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
    Connection conn = DriverManager.getConnection(DB.getURL(), DB.getUSER(), DB.getPASS());
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

}
