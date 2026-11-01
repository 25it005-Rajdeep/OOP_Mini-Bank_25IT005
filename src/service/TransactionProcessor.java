package service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Manages banking tasks using a thread pool
public class TransactionProcessor {

    // Create a pool containing 4 threads
    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    // Submit a task for execution
    public void submit(Runnable task) {
        executor.execute(task);
    }

    // Stop accepting tasks and wait for completion
    public void stop() {
        executor.shutdown();

        try {
            if (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}