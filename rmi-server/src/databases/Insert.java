package databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class Insert implements Callable<Object> {
  // Attributes
  private String query;

  // JDBC driver name and database URL
  private final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
  private Database DB;

  public Insert(String query, Database DB) {
    this.query = query;
    this.DB = DB;
  }

  @Override
  public Object call() throws ClassNotFoundException, SQLException {
    return insert(query);
  }

  public Integer insert(String query) throws SQLException, ClassNotFoundException {
    Integer key = 0;
    
    // Register JDBC driver
    Class.forName(JDBC_DRIVER);
    // Open a connection
    System.out.println("Connecting to a selected database...");
    Connection conn = DriverManager.getConnection(DB.getURL(), DB.getUSER(), DB.getPASS());
    System.out.println("Connected database successfully...");
    
    // Execute a query
    System.out.println("Inserting records into the table...");
    PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
    stmt.executeUpdate();

    ResultSet generatedKeys = stmt.getGeneratedKeys();
    if (generatedKeys.next()) {
      key = generatedKeys.getInt(1);
      System.out.println("Inserted records into the table...");
    }

    if (stmt != null)
      conn.close();

    if (conn != null)
      conn.close();

    System.out.println("Goodbye!");
    return key;
  }
}
