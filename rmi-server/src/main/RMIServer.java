package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Properties;
import databases.Database;

public class RMIServer {
  public static void main(String args[]) {
    Properties prop = new Properties();
    InputStream is = null;
    try {
      is = new FileInputStream(args[0]);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }
    try {
      prop.load(is);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(1);
    }

    Registry r = null;
    try {
      r = LocateRegistry.createRegistry(7654);
    } catch (RemoteException a) {
      a.printStackTrace();
      try {
        r = LocateRegistry.getRegistry(7654);
      } catch (NumberFormatException e1) {
        e1.printStackTrace();
      } catch (RemoteException e1) {
        e1.printStackTrace();
      }
    }

    // Get number of DBs
    Integer nDBS = Integer.parseInt(prop.getProperty("number-dbs"));

    ArrayList<Database> DBS = new ArrayList<>();

    for (int i = 1; i <= nDBS; i++) {
      DBS.add(new Database("jdbc:mysql://" + prop.getProperty("db-url" + i),
          prop.getProperty("db-user" + i), prop.getProperty("db-pass" + i)));
    }

    try {
      JDBCInterface jdbc = new JDBC(DBS);
      r.rebind("rmi-server", jdbc);
      System.out.println("RMIServer server ready");
    } catch (Exception e) {
      System.out.println("RMIServer server main " + e.getMessage());
    }
  }
}
