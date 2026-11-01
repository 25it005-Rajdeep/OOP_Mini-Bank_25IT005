import java.util.Scanner;

import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

import model.*;
import util.AnnotationValidator;
import service.*;
import exception.*;

// Main class of MiniBank application
public class MiniBank {

    // Bank information
    record BankInfo(String name, String branch) {}

    // Menu options
    enum MenuOption {
        OPEN_ACCOUNT, DEPOSIT, WITHDRAW, TRANSFER, EXIT
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Display bank information
        BankInfo bank = new BankInfo("MiniBank", "ITFive");

        System.out.println("===== " + bank.name() + " =====");
        System.out.println("Branch: " + bank.branch());

        // Create different types of accounts
        Account[] accounts = {
                new SavingsAccount("Rajdeep", 5000, 1000),
                new CurrentAccount("Rahul", 3000, 2000),
                new FixedDepositAccount("Amit", 10000)
        };

        // Display account details
        System.out.println("\n===== Account Details =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        // Demonstrate polymorphism
        System.out.println("\n===== Interest Rates =====");

        for (Account account : accounts) {
            System.out.println(
                    account.getOwnerName() + ": "
                            + account.interestRate() + "%"
            );
        }

        // Test banking transactions
        System.out.println("\n===== Transactions =====");

        try {
            accounts[0].deposit(1000);
            accounts[1].withdraw(500);
            accounts[2].withdraw(1000);
            accounts[2].deposit(2000);

            accounts[0].transfer(accounts[1], 500);

        } catch (InsufficientFundsException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Shortfall: Rs. " + e.getShortfall());

        } catch (InvalidAmountException e) {
            System.out.println("Error: " + e.getMessage());

        } catch (BankException e) {
            System.out.println("Bank Error: " + e.getMessage());

        } finally {
            System.out.println("Transaction processing finished.");
        }

        // Test annotation validation
        System.out.println("\n===== Annotation Validation =====");

        Account testAccount =
                new SavingsAccount("TestCustomer", -500, 0);

        String[] errors = AnnotationValidator.validate(testAccount);

        for (String error : errors) {
            System.out.println("Error: " + error);
        }

        // Test automatic resource closing
        System.out.println("\n===== Bank Session =====");

        try (BankSession session = new BankSession()) {
            System.out.println("Performing banking operations...");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // =====================================
        // HOUR 9: MULTITHREADING
        // =====================================

        System.out.println("\n===== Multithreading Test =====");

        Account raceAccount =
                new SavingsAccount("Thread Test", 0, 0);

        int threadCount = 10;
        int times = 1000;
        long amount = 1;

        Thread[] threads = new Thread[threadCount];

        // Create and start 10 threads
        for (int i = 0; i < threadCount; i++) {

            threads[i] = new Thread(
                    new AccountWorker(raceAccount, times, amount),
                    "Worker-" + (i + 1)
            );

            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("Expected Balance: "
                + (threadCount * times * amount));

        System.out.println("Actual Balance: "
                + raceAccount.getBalance());

        // =====================================
        // HOUR 10: EXECUTOR SERVICE
        // =====================================

        System.out.println("\n===== ExecutorService Test =====");

        Account taskAccount =
                new SavingsAccount("Executor Test", 5000, 0);

        TransactionProcessor processor =
                new TransactionProcessor();

        // Submit 10 deposit tasks
        for (int i = 0; i < 10; i++) {

            processor.submit(() -> {
                try {
                    taskAccount.deposit(100);
                } catch (BankException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            });
        }

        // Submit 5 withdrawal tasks
        for (int i = 0; i < 5; i++) {

            processor.submit(() -> {
                try {
                    taskAccount.withdraw(50);
                } catch (BankException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            });
        }

        // Wait for all tasks to finish
        processor.stop();

        System.out.println("Final Balance: Rs. "
                + taskAccount.getBalance());

        // =====================================
        // HOUR 10: PRODUCER-CONSUMER
        // =====================================

        System.out.println("\n===== Producer-Consumer Test =====");

        TransactionBuffer buffer = new TransactionBuffer();

        Account bufferAccount =
                new SavingsAccount("Buffer Test", 5000, 0);

        // Producer creates and adds transactions
        Thread producer = new Thread(() -> {

            try {
                for (int i = 0; i < 5; i++) {

                    buffer.put(() -> {
                        try {
                            bufferAccount.deposit(100);
                            System.out.println("Transaction processed.");
                        } catch (BankException e) {
                            System.out.println("Error: " + e.getMessage());
                        }
                    });
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

            } finally {
                buffer.finish();
                System.out.println("Producer finished.");
            }

        }, "Producer");

        // Consumer takes and processes transactions
        Thread consumer = new Thread(() -> {

            try {
                Runnable task;

                while ((task = buffer.take()) != null) {
                    task.run();
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            System.out.println("Consumer finished.");

        }, "Consumer");

        // Start both threads
        producer.start();
        consumer.start();

        // Wait for both threads
        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Final Buffer Account Balance: Rs. "
                + bufferAccount.getBalance());

        // =====================================
        // HOUR 10: DEADLOCK DEMONSTRATION
        // =====================================

        System.out.println("\n===== Deadlock Demonstration =====");

        Account accountA = new SavingsAccount("Account A", 5000, 0);
        Account accountB = new SavingsAccount("Account B", 5000, 0);

        // Demonstrate opposite-order locking
        DeadlockDemo.demonstrate(accountA, accountB);

        // Test the corrected transfer method
        System.out.println("\n===== Deadlock Prevention Test =====");

        try {
            accountA.transfer(accountB, 500);
        } catch (BankException e) {
            System.out.println("Transfer Error: " + e.getMessage());
        }

        System.out.println("Account A Balance: " + accountA.getBalance());
        System.out.println("Account B Balance: " + accountB.getBalance());

        // =====================================
        // HOUR 11: PERSISTENCE AND REPORTING
        // =====================================

        System.out.println("\n===== File Persistence Test =====");

        try {
            // Create folders for storing data and logs
            Files.createDirectories(Path.of("data"));
            Files.createDirectories(Path.of("logs"));

            Path accountFile = Path.of("data/accounts.dat");

            // Save accounts
            StatePersister.save(accounts, accountFile);

            // Load accounts into a new array
            Account[] loadedAccounts = StatePersister.load(accountFile);

            // Display loaded accounts
            System.out.println("\nLoaded Accounts:");

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            // Add transaction records to the log
            Path logFile = Path.of("logs/transactions.txt");

            TransactionLog.append(logFile, "DEPOSIT,1000");
            TransactionLog.append(logFile, "WITHDRAW,500");
            TransactionLog.append(logFile, "DEPOSIT,2000");
            TransactionLog.append(logFile, "WITHDRAW,300");

            // Generate end-of-day report
            Path reportFile = Path.of("data/daily-report.txt");

            ReportGenerator.generate(Path.of("logs"), reportFile);

            // Display generated report
            System.out.println("\n===== Daily Report =====");

            System.out.println(Files.readString(reportFile));

        } catch (IOException e) {
            System.out.println("File operation error: " + e.getMessage());
        }

        // =====================================
        // HOUR 12: COLLECTIONS FRAMEWORK
        // =====================================

        System.out.println("\n===== AccountStore Test =====");

        AccountStore store = new AccountStore();

        // Add accounts to the store
        for (Account account : accounts) {
            store.add(account);
        }

        // Add transaction history
        store.addTransaction("DEPOSIT,1000");
        store.addTransaction("WITHDRAW,500");

        // Display all accounts sorted by balance
        System.out.println("\nAccounts Sorted by Balance:");

        for (Account account : store.sortedByBalance()) {
            System.out.println(account);
        }

        // Display transaction history
        System.out.println("\nTransaction History:");

        for (String transaction : store.getTransactionHistory()) {
            System.out.println(transaction);
        }

        // Test account retrieval
        String searchNumber = accounts[0].getAccountNumber();

        Account foundAccount = store.get(searchNumber);

        if (foundAccount != null) {
            System.out.println("\nFound Account:");
            System.out.println(foundAccount);
        }

        // Test thread-safe account storage
        System.out.println("\n===== ConcurrentHashMap Test =====");

        TransactionProcessor concurrentProcessor =
                new TransactionProcessor();

        // Add accounts to ConcurrentHashMap
        for (Account account : accounts) {
            concurrentProcessor.addAccount(account);
        }

        // Retrieve an account
        Account sharedAccount = concurrentProcessor.getAccount(
                accounts[0].getAccountNumber()
        );

        System.out.println("Retrieved from ConcurrentHashMap:");
        System.out.println(sharedAccount);

        // Stop the processor
        concurrentProcessor.stop();

        // =====================================
        // MAIN BANKING MENU
        // =====================================

        int choice;

        do {
            System.out.println("\n===== MiniBank Menu =====");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");

            choice = sc.nextInt();

            // Handle menu selection
            String result = switch (choice) {
                case 1 -> "Open Account - Coming Soon";
                case 2 -> "Deposit - Coming Soon";
                case 3 -> "Withdraw - Coming Soon";
                case 4 -> "Transfer - Coming Soon";
                case 5 -> "Goodbye! Thank you for using MiniBank.";
                default -> "Invalid Choice! Please try again.";
            };

            System.out.println(result);

        } while (choice != 5);

        sc.close();
    }
}