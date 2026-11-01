package service;

import model.Account;
import exception.InvalidAmountException;

// Performs repeated deposits using a thread
public class AccountWorker implements Runnable {

    private Account account;
    private int times;
    private long amount;

    public AccountWorker(Account account, int times, long amount) {
        this.account = account;
        this.times = times;
        this.amount = amount;
    }

    @Override
    public void run() {

        System.out.println(Thread.currentThread().getName()
                + " started.");

        try {
            for (int i = 0; i < times; i++) {
                account.deposit(amount);
            }
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        }

        System.out.println(Thread.currentThread().getName()
                + " finished.");
    }
}