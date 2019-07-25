package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;

public class Delete implements Callable<Object> {
  // Attributes
  private String query;

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

  // Database
  private Database DB;

  public Delete(String query, Database DB) {
    this.query = query;
    this.DB = DB;
  }

  @Override
  public Object call() throws ClassNotFoundException, SQLException {
    return delete(query);
  }

  public Integer delete(String query) throws SQLException, ClassNotFoundException {
    Integer rows = 0;

    // Register JDBC driver
    Class.forName(JDBC_DRIVER);

    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB.getURL(), DB.getUSER(), DB.getPASS());
    System.out.println("Connected database successfully...");

    // STEP 4: Execute a query
    System.out.println("Deleting records from the table...");
    PreparedStatement stmt = conn.prepareStatement(query);

    rows = stmt.executeUpdate();
    System.out.println("Delete successful...");

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");
    return rows;
  }
}
