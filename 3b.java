import java.util.Scanner;
import java.util.concurrent.*;

public class MPISumArrayJava {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Take user input for array size (N) and number of threads
        System.out.print("Enter the size of the array (N): ");
        int N = scanner.nextInt();

        System.out.print("Enter the number of threads: ");
        int numThreads = scanner.nextInt();

        if (N <= 0 || numThreads <= 0) {
            System.out.println("Array size and number of threads must be positive.");
            return;
        }

        if (numThreads > N) {
            System.out.println("Number of threads should not exceed array size. Adjusting threads to array size.");
            numThreads = N;
        }

        int[] array = new int[N];

        // Fill the array with values from 1 to N
        for (int i = 0; i < N; i++) {
            array[i] = i + 1;
        }

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        int elementsPerThread = N / numThreads;
        int remainder = N % numThreads;

        @SuppressWarnings("unchecked")
        Future<Integer>[] results = new Future[numThreads];

        int start = 0;
        for (int i = 0; i < numThreads; i++) {
            int end = start + elementsPerThread + (i < remainder ? 1 : 0);

            final int startIndex = start, endIndex = end;
            results[i] = executor.submit(() -> {
                int localSum = 0;
                for (int j = startIndex; j < endIndex; j++) {
                    localSum += array[j];
                }
                System.out.println("Thread " + Thread.currentThread().getId() + " computed local sum = " + localSum);
                return localSum;
            });

            start = end; // update start for next thread
        }

        // Compute final sum
        int totalSum = 0;
        for (Future<Integer> result : results) {
            try {
                totalSum += result.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        System.out.println("Total sum of array elements = " + totalSum);
    }
}
