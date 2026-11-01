import model.*;
import service.*;
import exception.*;

import java.util.Scanner;
import java.nio.file.Path;
import java.nio.file.Files;
import java.io.IOException;

// Main class of MiniBank application
public class MiniBank {

    // Bank information record
    record BankInfo(String bankName, String branchName) {}

    // Menu options
    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bankInfo = new BankInfo("MiniBank", "ITFive");

        System.out.println("================================");
        System.out.println("       WELCOME TO MINIBANK");
        System.out.println("================================");
        System.out.println("Bank: " + bankInfo.bankName());
        System.out.println("Branch: " + bankInfo.branchName());

        // File used to store account information
        Path accountFile = Path.of("data/accounts.dat");

        // Create data folder if it does not exist
        try {
            Files.createDirectories(Path.of("data"));
            Files.createDirectories(Path.of("logs"));
        } catch (IOException e) {
            System.out.println("Folder creation error: " + e.getMessage());
        }

        // Load previously saved accounts
        Account[] accounts;

        if (Files.exists(accountFile)) {

            accounts = StatePersister.load(accountFile);

            if (accounts.length == 0) {
                accounts = createDefaultAccounts();
            }

        } else {
            accounts = createDefaultAccounts();
        }

        System.out.println("\nAccounts loaded successfully.");

        // Create banking service
        BankService bankService = new BankService();
        bankService.loadAccounts(accounts);

        int choice = 0;

        // Main banking menu
        do {

            System.out.println("\n========== MINIBANK MENU ==========");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Account Statement");
            System.out.println("6. List All Accounts");
            System.out.println("7. Batch Transactions");
            System.out.println("8. Generate Report");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    // Open account
                    case 1 -> {

                        System.out.print("Enter customer name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter initial balance: ");
                        long balance = Long.parseLong(sc.nextLine());

                        Account account =
                                bankService.openAccount(name, balance);

                        System.out.println("\nAccount created successfully!");
                        System.out.println(account);
                    }

                    // Deposit
                    case 2 -> {

                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine();

                        System.out.print("Enter deposit amount: ");
                        long amount = Long.parseLong(sc.nextLine());

                        bankService.deposit(accountNumber, amount);

                        System.out.println("Deposit successful!");
                        System.out.println(
                                bankService.statement(accountNumber)
                        );
                    }

                    // Withdraw
                    case 3 -> {

                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine();

                        System.out.print("Enter withdrawal amount: ");
                        long amount = Long.parseLong(sc.nextLine());

                        bankService.withdraw(accountNumber, amount);

                        System.out.println("Withdrawal successful!");
                        System.out.println(
                                bankService.statement(accountNumber)
                        );
                    }

                    // Transfer
                    case 4 -> {

                        System.out.print("Enter sender account number: ");
                        String from = sc.nextLine();

                        System.out.print("Enter receiver account number: ");
                        String to = sc.nextLine();

                        System.out.print("Enter transfer amount: ");
                        long amount = Long.parseLong(sc.nextLine());

                        bankService.transfer(from, to, amount);

                        System.out.println("Transfer completed!");
                    }

                    // Account statement
                    case 5 -> {

                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine();

                        System.out.println(
                                bankService.statement(accountNumber)
                        );
                    }

                    // List accounts
                    case 6 -> {

                        System.out.println("\nAll Bank Accounts:");

                        for (Account account : bankService.listAccounts()) {
                            System.out.println(account);
                        }
                    }

                    // Batch transactions
                    case 7 -> {

                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine();

                        System.out.print("Enter number of deposits: ");
                        int count = Integer.parseInt(sc.nextLine());

                        System.out.print("Enter amount for each deposit: ");
                        long amount = Long.parseLong(sc.nextLine());

                        TransactionProcessor processor =
                                new TransactionProcessor();

                        // Add accounts to the thread-safe store
                        for (Account account : bankService.listAccounts()) {
                            processor.addAccount(account);
                        }

                        Account batchAccount =
                                processor.getAccount(accountNumber);

                        if (batchAccount == null) {

                            System.out.println("Account not found.");

                        } else {

                            // Submit multiple deposit tasks
                            for (int i = 0; i < count; i++) {

                                processor.submit(() -> {
                                    try {
                                        batchAccount.deposit(amount);
                                    } catch (InvalidAmountException e) {
                                        System.out.println(e.getMessage());
                                    }
                                });
                            }

                            // Wait for submitted tasks to finish
                            processor.stop();

                            System.out.println(
                                    "Batch transactions completed."
                            );

                            System.out.println(
                                    bankService.statement(accountNumber)
                            );
                        }
                    }

                    // Generate report
                    case 8 -> {

                        Path logsFolder = Path.of("logs");
                        Path reportFile =
                                Path.of("data/daily-report.txt");

                        ReportGenerator.generate(
                                logsFolder,
                                reportFile
                        );

                        System.out.println("Report generated.");

                        try {
                            if (Files.exists(reportFile)) {
                                System.out.println(
                                        Files.readString(reportFile)
                                );
                            }
                        } catch (IOException e) {
                            System.out.println(
                                    "Report reading error: " + e.getMessage()
                            );
                        }
                    }

                    // Exit
                    case 9 -> {

                        // Save all accounts before closing
                        StatePersister.save(
                                bankService.getAccounts(),
                                accountFile
                        );

                        System.out.println(
                                "Account data saved successfully."
                        );

                        System.out.println(
                                "Thank you for using MiniBank!"
                        );
                    }

                    default ->
                            System.out.println("Invalid choice. Try again.");
                }

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");

            } catch (BankException e) {

                System.out.println("Bank Error: " + e.getMessage());

            } catch (IllegalArgumentException e) {

                System.out.println("Error: " + e.getMessage());
            }

        } while (choice != 9);

        sc.close();
    }

    // Create initial accounts for a new installation
    private static Account[] createDefaultAccounts() {

        return new Account[]{
                new SavingsAccount("Rajdeep", 5000, 1000),
                new CurrentAccount("Rahul", 3000, 2000),
                new FixedDepositAccount("Amit", 10000)
        };
    }
}