package service;

import model.*;
import exception.*;
import util.Validator;

import java.util.ArrayList;

// Handles the main banking operations
public class BankService {

    private Repository<Account> repository =
            new Repository<>();

    // Open a new savings account
    public Account openAccount(String name, long initialBalance) {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        if (initialBalance < 0) {
            throw new IllegalArgumentException("Invalid initial balance.");
        }

        Account account =
                new SavingsAccount(name, initialBalance, 0);

        repository.save(account);

        return account;
    }

    // Deposit money
    public void deposit(String accountNumber, long amount)
            throws InvalidAmountException {

        Account account = findAccount(accountNumber);
        account.deposit(amount);
    }

    // Withdraw money
    public void withdraw(String accountNumber, long amount)
            throws BankException {

        Account account = findAccount(accountNumber);
        account.withdraw(amount);
    }

    // Transfer money
    public void transfer(String from, String to, long amount)
            throws BankException {

        Account source = findAccount(from);
        Account destination = findAccount(to);

        source.transfer(destination, amount);
    }

    // Display account statement
    public String statement(String accountNumber) {

        Account account = findAccount(accountNumber);

        return account.toString();
    }

    // Display all accounts
    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    // Find account or throw an exception
    private Account findAccount(String accountNumber) {

        Account account = repository.findById(accountNumber);

        if (account == null) {
            throw new IllegalArgumentException(
                    "Account not found: " + accountNumber
            );
        }

        return account;
    }

    // Load accounts into the repository
    public void loadAccounts(Account[] accounts) {
        repository.saveAll(accounts);
    }

    // Return accounts for saving
    public Account[] getAccounts() {
        return repository.findAll().toArray(new Account[0]);
    }
}