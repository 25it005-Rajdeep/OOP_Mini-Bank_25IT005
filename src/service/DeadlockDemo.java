package service;

import model.Account;
import exception.BankException;

import java.util.concurrent.CountDownLatch;

// Demonstrates deadlock when accounts are locked in opposite order
public class DeadlockDemo {

    public static void demonstrate(Account accountA, Account accountB) {

        CountDownLatch bothLocked = new CountDownLatch(2);

        Thread thread1 = new Thread(() -> {
            synchronized (accountA) {
                System.out.println("Thread 1 locked Account A.");
                bothLocked.countDown();

                try {
                    bothLocked.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                synchronized (accountB) {
                    System.out.println("Thread 1 transferring A to B.");
                }
            }
        }, "Transfer-A-to-B");

        Thread thread2 = new Thread(() -> {
            synchronized (accountB) {
                System.out.println("Thread 2 locked Account B.");
                bothLocked.countDown();

                try {
                    bothLocked.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }

                synchronized (accountA) {
                    System.out.println("Thread 2 transferring B to A.");
                }
            }
        }, "Transfer-B-to-A");

        // Daemon threads won't prevent the application from exiting
        thread1.setDaemon(true);
        thread2.setDaemon(true);

        thread1.start();
        thread2.start();
    }
}