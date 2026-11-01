package test;

import model.*;
import exception.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BankServiceTest {

    // Test 1: Deposit should increase balance
    @Test
    void testDeposit() throws InvalidAmountException {
        Account account = new SavingsAccount("TestUser", 1000, 0);
        account.deposit(500);
        assertEquals(1500, account.getBalance());
    }

    // Test 2: Withdrawal beyond limit should throw exception
    @Test
    void testInsufficientFunds() {
        Account account = new SavingsAccount("TestUser", 2000, 1000);
        assertThrows(InsufficientFundsException.class, () -> account.withdraw(1500));
    }

    // Test 3: Concurrent deposits should maintain correct balance
    @Test
    void testConcurrentDeposits() throws InterruptedException {

        Account account = new SavingsAccount("TestUser", 0, 0);

        Thread[] threads = new Thread[10];

        for (int i = 0; i < 10; i++) {

            threads[i] = new Thread( () -> {
                try {
                    for (int j = 0; j < 1000; j++) {
                        account.deposit(1);
                    }
                } catch (InvalidAmountException e) {
                    fail(e.getMessage());
                }
            });

            threads[i].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        assertEquals(10000, account.getBalance());
    }
}