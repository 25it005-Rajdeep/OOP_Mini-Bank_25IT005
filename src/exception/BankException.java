package exception;

// Base class for all bank-related exceptions
public class BankException extends Exception {

    public BankException(String message) {
        super(message);
    }
}