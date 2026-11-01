package exception;

// Thrown when the transaction amount is invalid
public class InvalidAmountException extends BankException {

    public InvalidAmountException(String message) {
        super(message);
    }
}