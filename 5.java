// Process.java
public class Process extends Thread {
    private int id;
    private boolean hasToken;
    private boolean wantToEnterCS;
    private Process next;
    private TokenRing controller;

    public Process(int id, TokenRing controller) {
        this.id = id;
        this.controller = controller;
        this.hasToken = false;
        this.wantToEnterCS = false;
    }

    public void setNext(Process next) {
        this.next = next;
    }

    public void receiveToken() {
        if (controller.shouldTerminate()) return;

        hasToken = true;
        wantToEnterCS = true;  // Auto request CS when receiving token
        if (wantToEnterCS) {
            enterCriticalSection();
            wantToEnterCS = false;
        }
        controller.incrementTokenPasses();
        passToken();
    }

    public void enterCriticalSection() {
        System.out.println("Process " + id + " entering critical section.");
        try {
            Thread.sleep(500);  // Simulate some work
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Process " + id + " leaving critical section.");
    }

    public void passToken() {
        hasToken = false;
        if (!controller.shouldTerminate()) {
            System.out.println("Process " + id + " passing token to Process " + next.id);
            next.receiveToken();
        } else {
            System.out.println("Maximum token passes reached. Terminating simulation.");
            System.exit(0);
        }
    }

    @Override
    public void run() {
        // No need to loop, all logic handled by token passing
    }
}

// TokenRing.java
public class TokenRing {
    private static final int MAX_TOKEN_PASSES = 6;
    private int tokenPasses = 0;

    public synchronized void incrementTokenPasses() {
        tokenPasses++;
    }

    public synchronized boolean shouldTerminate() {
        return tokenPasses >= MAX_TOKEN_PASSES;
    }

    public static void main(String[] args) {
        int n = 6;  // Fixed 6 processes
        TokenRing controller = new TokenRing();

        Process[] processes = new Process[n];

        // Create processes
        for (int i = 0; i < n; i++) {
            processes[i] = new Process(i, controller);
        }

        // Link processes in ring
        for (int i = 0; i < n; i++) {
            processes[i].setNext(processes[(i + 1) % n]);
        }

        // Start all processes
        for (int i = 0; i < n; i++) {
            processes[i].start();
        }

        // Give initial token to process 0
        processes[0].receiveToken();
    }
}


// javac Process.java TokenRing.java
// java TokenRing
