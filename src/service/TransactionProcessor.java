package service;

import model.Account;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// Manages banking tasks and shared accounts
public class TransactionProcessor {

    // Thread-safe account storage
    private ConcurrentHashMap<String, Account> accounts =
            new ConcurrentHashMap<>();

    // Create a pool containing 4 threads
    private final ExecutorService executor =
            Executors.newFixedThreadPool(4);

    // Add an account
    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // Find an account
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Submit a task
    public void submit(Runnable task) {
        executor.execute(task);
    }

    // Stop the thread pool
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