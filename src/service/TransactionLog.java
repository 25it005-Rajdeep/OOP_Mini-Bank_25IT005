package service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

// Adds transaction records without deleting old records
public class TransactionLog {

    public static void append(Path file, String line) {

        try {
            // Create the file if needed and append the new line
            Files.write(
                    file,
                    (line + System.lineSeparator()).getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            System.out.println("Error writing transaction log: "
                    + e.getMessage());
        }
    }
}