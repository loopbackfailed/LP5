// BerkeleyCoordinator.java
import java.io.*;
import java.net.*;
import java.util.*;

public class BerkeleyCoordinator {
    private static final int PORT = 12345;  // Port for communication
    private static final int CLIENT_COUNT = 3;  // Number of clients

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Coordinator started, waiting for clients...");

            // List to store the client sockets
            List<Socket> clientSockets = new ArrayList<>();

            // Accept connections from clients
            for (int i = 0; i < CLIENT_COUNT; i++) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client " + (i + 1) + " connected.");
                clientSockets.add(clientSocket);
            }

            // Get times from clients and compute average
            List<Long> clientTimes = new ArrayList<>();
            for (Socket clientSocket : clientSockets) {
                DataInputStream in = new DataInputStream(clientSocket.getInputStream());
                long clientTime = in.readLong();
                clientTimes.add(clientTime);
                System.out.println("Received time from client: " + clientTime);
            }

            // Calculate average time
            long sum = 0;
            for (long time : clientTimes) {
                sum += time;
            }
            long averageTime = sum / clientTimes.size();
            System.out.println("Average time calculated by coordinator: " + averageTime);

            // Send offset back to each client
            for (int i = 0; i < CLIENT_COUNT; i++) {
                long offset = averageTime - clientTimes.get(i);
                Socket clientSocket = clientSockets.get(i);
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeLong(offset);
                System.out.println("Sending offset to client " + (i + 1) + ": " + offset);
            }

            // Close client connections
            for (Socket clientSocket : clientSockets) {
                clientSocket.close();
            }
            System.out.println("Clock synchronization completed.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// BerkeleyClient.java
import java.io.*;
import java.net.*;
import java.util.*;

public class BerkeleyClient {
    private static final String COORDINATOR_HOST = "localhost";  // Coordinator's host
    private static final int COORDINATOR_PORT = 12345;  // Coordinator's port

    public static void main(String[] args) {
        try (Socket socket = new Socket(COORDINATOR_HOST, COORDINATOR_PORT)) {
            System.out.println("Client started, sending local time to coordinator...");

            // Simulating a clock with random drift
            Random random = new Random();
            long localTime = System.currentTimeMillis() + random.nextInt(10000);  // Adding some random drift
            System.out.println("Client's local time: " + localTime);

            // Send the local time to the coordinator
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeLong(localTime);
            // Wait for the time offset from the coordinator
            DataInputStream in = new DataInputStream(socket.getInputStream());
            long offset = in.readLong();
            System.out.println("Received offset from coordinator: " + offset);

            // Adjust local time by the offset
            long synchronizedTime = localTime + offset;
            System.out.println("Adjusted time: " + synchronizedTime);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// javac BerkeleyCoordinator.java
// javac BerkeleyClient.java


// java BerkeleyCoordinator


// java BerkeleyClient

