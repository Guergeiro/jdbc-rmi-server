package main;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/* arg0 -> db url
 * arg1 -> db username
 * arg2 -> db password */

public class RMIServer {
  public static void main(String args[]) {
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

    try {
        JDBCInterface jdbc = new JDBC("db", "root", "a1s2d3f4A");
        r.rebind("jdbc", jdbc);
        System.out.println("RMIServer server ready");
    } catch (Exception e) {
        System.out.println("RMIServer server main " + e.getMessage());
    }
}
}
