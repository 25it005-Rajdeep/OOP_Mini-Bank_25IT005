package util;
import model.Account;

public class StatementFormatter {

    public static String buildStatement(Account account) {

        StringBuilder sb = new StringBuilder();

        sb.append("===== ACCOUNT STATEMENT =====\n");
        sb.append("model.Account Number: ").append(account.getAccountNumber()).append("\n");
        sb.append("Owner Name: ").append(account.getOwnerName()).append("\n");
        sb.append("Balance: Rs. ").append(account.getBalance()).append("\n");
        sb.append("=============================");

        return sb.toString();
    }
}