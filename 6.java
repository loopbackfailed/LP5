// BullyElection.java
import java.util.Scanner;
class Process {
    int id;
    boolean isActive;

    Process(int id) {
        this.id = id;
        this.isActive = true;
    }
}

public class BullyElection {
    static Process[] processes;
    static int coordinator = -1;

    public static void initiateElection(int initiatorId) {
        System.out.println("\nProcess " + initiatorId + " initiates election...");
        boolean higherResponded = false;

        for (int i = initiatorId + 1; i < processes.length; i++) {
            if (processes[i].isActive) {
                System.out.println("Process " + initiatorId + " sends ELECTION to Process " + i);
                System.out.println("Process " + i + " responds OK");
                higherResponded = true;
            }
        }

        if (!higherResponded) {
            coordinator = initiatorId;
            System.out.println("Process " + initiatorId + " becomes coordinator.");
            broadcastCoordinator();
        } else {
            for (int i = processes.length - 1; i > initiatorId; i--) {
                if (processes[i].isActive) {
                    initiateElection(i);
                    break;
                }
            }
        }
    }

    public static void broadcastCoordinator() {
        for (int i = 0; i < processes.length; i++) {
            if (i != coordinator && processes[i].isActive)
                System.out.println("Process " + coordinator + " informs Process " + i + " that it is the new coordinator.");
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        int n = sc.nextInt();
        processes = new Process[n];

        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i);
        }

        System.out.print("Enter coordinator process ID: ");
        coordinator = sc.nextInt();

        System.out.print("Enter process ID to crash: ");
        int crashedId = sc.nextInt();
        processes[crashedId].isActive = false;

        System.out.print("Enter initiator process ID for election: ");
        int initiatorId = sc.nextInt();

        initiateElection(initiatorId);
        sc.close();
    }
}

// RingElection.java
import java.util.*;

class RingProcess {
    int id;
    boolean isActive;

    RingProcess(int id) {
        this.id = id;
        this.isActive = true;
    }
}

public class RingElection {
    static RingProcess[] processes;
    static int n;

    public static void initiateElection(int initiatorId) {
        System.out.println("\nProcess " + initiatorId + " initiates RING election...");
        List<Integer> message = new ArrayList<>();
        int current = initiatorId;

        do {
            if (processes[current].isActive) {
                message.add(processes[current].id);
                System.out.println("Election message passed through Process " + current);
            }
            current = (current + 1) % n;
        } while (current != initiatorId);

        int newCoordinator = Collections.max(message);
        System.out.println("New coordinator elected: Process " + newCoordinator);

        int notify = (initiatorId + 1) % n;
        do {
            if (processes[notify].isActive) {
                System.out.println("Coordinator message passed to Process " + notify);
            }
            notify = (notify + 1) % n;
        } while (notify != initiatorId);
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter number of processes: ");
        n = sc.nextInt();
        processes = new RingProcess[n];

        for (int i = 0; i < n; i++) {
            processes[i] = new RingProcess(i);
        }

        System.out.print("Enter process ID to crash: ");
        int crashed = sc.nextInt();
        processes[crashed].isActive = false;

        System.out.print("Enter initiator process ID: ");
        int initiator = sc.nextInt();

        initiateElection(initiator);
        sc.close();
    }
}



// javac BullyElection.java
// javac RingElection.java

// java BullyElection

// java RingElection
