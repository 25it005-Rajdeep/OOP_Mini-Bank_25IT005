package model;

import model.annotation.Id;
import model.annotation.Positive;
import model.annotation.MaxLength;
import exception.*;
import java.io.Serializable;

// Abstract parent class for all account types
public abstract class Account implements Transactable, InterestBearing, Serializable {

    // Serialization version
    private static final long serialVersionUID = 1L;
    // Account identification and details
    @Id
    private final String accountNumber;

    @MaxLength(30)
    private String ownerName;

    @Positive
    private long balance;

    private boolean active;

    private static int accountCounter = 0;

    // Generate unique account numbers
    private static String generateAccountNumber() {
        accountCounter++;
        return String.format("AC%04d", accountCounter);
    }

    // Constructor with balance
    public Account(String ownerName, long balance) {
        this.accountNumber = generateAccountNumber();
        this.ownerName = ownerName;
        this.balance = balance;
        this.active = true;
    }

    // Constructor without balance
    public Account(String ownerName) {
        this(ownerName, 0);
    }

    // Deposit money into the account
    @Override
    public synchronized void deposit(long amount) throws InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be greater than zero.");
        }

        balance += amount;
    }

    // Withdraw money from the account
    @Override
    public synchronized boolean withdraw(long amount) throws InsufficientFundsException, InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be greater than zero.");
        }

        if (!canWithdraw(amount)) {

            long shortfall = Math.max(0, amount - balance);

            throw new InsufficientFundsException(
                    "Insufficient balance.", shortfall);
        }

        balance -= amount;
        return true;
    }

    // Transfer money using a consistent locking order
    public void transfer(Account to, long amount) throws BankException {

        if (to == null) {
            throw new AccountNotFoundException(
                    "Destination account not found.");
        }

        // Decide which account to lock first
        Account firstLock = this;
        Account secondLock = to;

        if (this.accountNumber.compareTo(to.accountNumber) > 0) {
            firstLock = to;
            secondLock = this;
        }

        // Always lock accounts in the same order
        synchronized (firstLock) {
            synchronized (secondLock) {

                try {
                    withdraw(amount);
                    to.deposit(amount);

                    System.out.println("Transfer successful.");

                } catch (BankException e) {
                    throw e;

                } finally {
                    System.out.println("Transfer operation completed.");
                }
            }
        }
    }

    // Get account details
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    // Convert account details into a string
    @Override
    public String toString() {
        return "Account Number: " + accountNumber
                + ", Owner: " + ownerName
                + ", Balance: Rs. " + balance;
    }

    // Compare accounts using account number
    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Account account = (Account) o;

        return accountNumber.equals(account.accountNumber);
    }

    // Generate hash code using account number
    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    // Methods implemented by child classes
    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);
}