// HelloInterface.java
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface HelloInterface extends Remote {
    String say(String clientName) throws RemoteException;
}
// Hello.java
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Hello extends UnicastRemoteObject implements HelloInterface {

    public Hello() throws RemoteException {
        super(); // Export object on anonymous port
    }

    // Each client will call this with their name
    public String say(String clientName) throws RemoteException {
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        System.out.println("Client " + clientName + " invoked say() at " + time);
        return "Hello, " + clientName + "! [Server time: " + time + "]";
    }
}
//  HelloServer.java
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class HelloServer {
    public static void main(String[] args) {
        try {
            // Start RMI Registry
            LocateRegistry.createRegistry(1099); // default port
            Hello obj = new Hello();
            Naming.rebind("HelloService", obj);
            System.out.println("HelloServer is ready.");
        } catch (RemoteException re) {
            System.err.println("RemoteException: " + re);
        } catch (Exception e) {
            System.err.println("Exception: " + e);
        }
    }
}
// HelloClient.java
import java.rmi.Naming;

public class HelloClient {

    static class ClientWorker extends Thread {
        private String name;

        public ClientWorker(String name) {
            this.name = name;
        }

        public void run() {
            try {
                HelloInterface stub = (HelloInterface) Naming.lookup("//localhost/HelloService");
                String response = stub.say(name);
                System.out.println("Response for " + name + ": " + response);
            } catch (Exception e) {
                System.err.println("Exception for " + name + ": " + e);
            }
        }
    }
    public static void main(String[] args) {
        // Simulate 5 concurrent clients
        for (int i = 1; i <= 5; i++) {
            new ClientWorker("Client" + i).start();
        }
    }
}

