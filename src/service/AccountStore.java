package service;

import model.Account;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Comparator;

// Stores accounts and transaction history
public class AccountStore {

    // Store accounts using account numbers as keys
    private HashMap<String, Account> accounts = new HashMap<>();

    // Store transaction history
    private ArrayList<String> transactionHistory = new ArrayList<>();

    // Add an account
    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // Find an account using its account number
    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Get all accounts
    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    // Add a transaction to history
    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    // Get transaction history
    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    // Return accounts sorted by balance
    public ArrayList<Account> sortedByBalance() {

        ArrayList<Account> sortedAccounts = getAll();

        sortedAccounts.sort(
                Comparator.comparingLong(Account::getBalance)
        );

        return sortedAccounts;
    }
}