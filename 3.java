// ParallelSum.java
import java.util.Random;

public class ParallelSum {
    static final int N = 100;  // Total number of elements in the array
    static final int NUM_THREADS = 4;  // Number of threads (processors)

    // Array to store the values
    static int[] array = new int[N];
    // Array to store partial sums computed by each thread
    static int[] partialSums = new int[NUM_THREADS];

    // Task for each thread: calculate the sum of a chunk of the array
    static class SumTask extends Thread {
        private int start;
        private int end;
        private int threadIndex;

        public SumTask(int start, int end, int threadIndex) {
            this.start = start;
            this.end = end;
            this.threadIndex = threadIndex;
        }

        @Override
        public void run() {
            int sum = 0;
            for (int i = start; i < end; i++) {
                sum += array[i];
            }
            // Store the partial sum for this thread
            partialSums[threadIndex] = sum;
            System.out.println("Thread " + threadIndex + " partial sum: " + sum);
        }
    }

    public static void main(String[] args) {
        // Initialize the array with random values between 0 and 99
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            array[i] = rand.nextInt(100);  // Random values between 0 and 99
        }

        // Divide the work between the threads
        int chunkSize = N / NUM_THREADS;
        Thread[] threads = new Thread[NUM_THREADS];

        // Start threads for summing chunks of the array
        for (int i = 0; i < NUM_THREADS; i++) {
            int start = i * chunkSize;
            int end = (i == NUM_THREADS - 1) ? N : (i + 1) * chunkSize; // Last thread handles any leftover elements
            threads[i] = new SumTask(start, end, i);
            threads[i].start();
        }

        // Wait for all threads to finish
        try {
            for (int i = 0; i < NUM_THREADS; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Calculate the total sum by aggregating the partial sums
        int totalSum = 0;
        for (int sum : partialSums) {
            totalSum += sum;
        }
        // Display the total sum
        System.out.println("Total sum of the array: " + totalSum);
    }
}

// javac ParallelSum.java
// java ParallelSum
