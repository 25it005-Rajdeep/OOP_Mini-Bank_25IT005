package service;

import model.Account;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.nio.file.Path;

// Saves and loads account data
public class StatePersister {

    // Save accounts to a file
    public static void save(Account[] accounts, Path file) {

        try (ObjectOutputStream output =
                     new ObjectOutputStream(
                             new FileOutputStream(file.toFile()))) {

            output.writeObject(accounts);
            System.out.println("Accounts saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving accounts: " + e.getMessage());
        }
    }

    // Load accounts from a file
    public static Account[] load(Path file) {

        try (ObjectInputStream input =
                     new ObjectInputStream(
                             new FileInputStream(file.toFile()))) {

            return (Account[]) input.readObject();

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading accounts: " + e.getMessage());
            return new Account[0];
        }
    }
}