package service;

// Automatically closes the banking session
public class BankSession implements AutoCloseable {

    public BankSession() {
        System.out.println("Bank session started.");
    }

    @Override
    public void close() {
        System.out.println("Bank session closed.");
    }
}