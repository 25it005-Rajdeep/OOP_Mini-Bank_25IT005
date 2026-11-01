import model.*;
// Import account classes
import model.Account;
import model.SavingsAccount;
import model.CurrentAccount;
import model.FixedDepositAccount;

// Import utility classes
import service.BankSession;
import util.* ;

// Import withdrawal rule
import service.WithdrawRule;

// Static import example
import static java.lang.System.out;
import java.util.Scanner;
import exception.*;

public class MiniBank {

    public static void main(String[] args) {
        BankInfo bank = new BankInfo("MiniBank", "ITFive Branch");
        Scanner sc = new Scanner(System.in);
        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

        Account[] accounts = {
                new SavingsAccount("Rajdeep", 5000, 1000),
                new CurrentAccount("Rahul", 3000, 2000),
                new FixedDepositAccount("Amit", 10000)
        };
        // Test annotation validation
        System.out.println("\n===== Annotation Validation =====");

        // Negative balance for testing
        Account testAccount =
                new SavingsAccount("TestCustomer", -500, 0);

        // Validate the account
        String[] errors = AnnotationValidator.validate(testAccount);

        // Print validation errors
        for (String error : errors) {
            System.out.println("Error: " + error);
        }

        System.out.println("\n===== Anonymous Class =====");
        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        System.out.println("Withdrawal allowed: " + rule1.allow(accounts[0], 1000));

        System.out.println("\n===== Lambda Expression =====");
        WithdrawRule rule2 = (account, amount) -> account.canWithdraw(amount);

        System.out.println("Withdrawal allowed: " + rule2.allow(accounts[0], 1000));

        System.out.println("\n===== Yearly Interest =====");

        for (Account account : accounts) {
            System.out.println(account.getOwnerName() + ": Rs. " + account.yearlyInterest());
        }

        System.out.println("\n===== model.Account Interest Rates =====");
        for (Account account : accounts) {

            System.out.println(account.getOwnerName() + " Interest Rate: " + account.interestRate() + "%");

            if (account instanceof SavingsAccount savings) {
                System.out.println("This is a Savings model.Account.");
                System.out.println("Balance: Rs. " + savings.getBalance());
            }
        }

        // Perform banking transactions
        System.out.println("\n===== Transactions =====");

        try {

            accounts[0].deposit(1000);

            accounts[1].withdraw(500);

            accounts[2].withdraw(1000);

            accounts[2].deposit(2000);

            // Transfer money between accounts
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

        System.out.println("\n===== model.Account Details =====");

        for (Account account : accounts) {
            System.out.println("model.Account Number: " + account.getAccountNumber());
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Balance: Rs. " + account.getBalance());
            System.out.println("-------------------------");
        }

        System.out.println("\n===== toString() Test =====");

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("\n===== equals() Test =====");
        System.out.println("model.Account 1 equals model.Account 2: " + accounts[0].equals(accounts[1]));

        System.out.println("\n===== instanceof Test =====");
        System.out.println("Is model.Account object? " + (accounts[0] instanceof Account));

        // Try-with-resources example
        System.out.println("\n===== Bank Session =====");

        try (BankSession session = new BankSession()) {

            System.out.println("Performing banking operations...");

        } catch (Exception e) {

            System.out.println("Error: " + e.getMessage());
        }

        int choice;

        System.out.println("\n===== util.Validator Testing =====");

        System.out.println("Valid Mobile: " + Validator.isValidMobile("9876543210"));
        System.out.println("Invalid Mobile: " + Validator.isValidMobile("12345"));

        System.out.println("Valid Email: " + Validator.isValidEmail("test@gmail.com"));
        System.out.println("Invalid Email: " + Validator.isValidEmail("testgmail.com"));

        System.out.println("Valid PAN: " + Validator.isValidPan("ABCDE1234F"));
        System.out.println("Invalid PAN: " + Validator.isValidPan("12345"));

        System.out.println("Valid IFSC: " + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("Invalid IFSC: " + Validator.isValidIfsc("12345"));

        System.out.println("\n===== model.Command Parser Testing =====");
        Command command = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("Type: " + command.type());
        System.out.println("model.Account Number: " + command.accountNumber());
        System.out.println("Amount: " + command.amount());
        System.out.println("\n===== Statement Testing =====");

        System.out.println(StatementFormatter.buildStatement(accounts[0]));
        do {
            System.out.println("\n===== MiniBank Menu =====");
            System.out.println("1. Open model.Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Open model.Account");
                    break;

                case 2:
                    System.out.println("Deposit");
                    break;

                case 3:
                    System.out.println("Withdraw");
                    break;

                case 4:
                    System.out.println("Transfer");
                    break;

                case 5:
                    System.out.println("Thank you for using R's Bank.\nHave a nice day...!!");
                    break;

                default:
                    System.out.println("Invalid Choice");
            }

        } while (choice != 5);
        sc.close();
    }
    record BankInfo(String name, String branch) {
    }
    enum MenuOption {
        OPEN_ACCOUNT,
        DEPOSIT,
        WITHDRAW,
        TRANSFER,
        EXIT
    }
}