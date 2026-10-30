import java.util.Scanner;
public class MiniBank {

    public static void main(String[] args) {
        BankInfo bank = new BankInfo("MiniBank", "ITFive Branch");
        Scanner sc = new Scanner(System.in);
        System.out.println("================================");
        System.out.println("       " + bank.name());
        System.out.println("       " + bank.branch());
        System.out.println("================================");

        int choice;
        do {
            System.out.println("\n===== MiniBank Menu =====");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Exit");

            System.out.print("Enter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Open Account");
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
                    System.out.println("Goodbye! Thank you for using MiniBank.");
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