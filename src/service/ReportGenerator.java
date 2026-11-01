package service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

// Generates an end-of-day transaction report
public class ReportGenerator {

    public static void generate(Path logsFolder, Path reportFile) {

        long totalDeposits = 0;
        long totalWithdrawals = 0;

        StringBuilder report = new StringBuilder();

        report.append("===== END OF DAY REPORT =====\n");

        // Read all files inside the logs folder
        try (DirectoryStream<Path> files =
                     Files.newDirectoryStream(logsFolder)) {

            for (Path file : files) {

                if (!Files.isRegularFile(file)) {
                    continue;
                }

                report.append("\nFile: ")
                        .append(file.getFileName());

                // Read file details
                BasicFileAttributes attributes =
                        Files.readAttributes(
                                file,
                                BasicFileAttributes.class
                        );

                report.append("\nSize: ")
                        .append(attributes.size())
                        .append(" bytes");

                report.append("\nLast Modified: ")
                        .append(attributes.lastModifiedTime());

                // Read transaction records
                try (BufferedReader reader =
                             Files.newBufferedReader(file)) {

                    String line;

                    while ((line = reader.readLine()) != null) {

                        report.append("\n").append(line);

                        String[] parts = line.split(",");

                        if (parts.length >= 2) {
                            try {
                                long amount =
                                        Long.parseLong(parts[1].trim());

                                if (parts[0].trim().equalsIgnoreCase("DEPOSIT")) {
                                    totalDeposits += amount;

                                } else if (parts[0].trim()
                                        .equalsIgnoreCase("WITHDRAW")) {
                                    totalWithdrawals += amount;
                                }

                            } catch (NumberFormatException e) {
                                System.out.println(
                                        "Invalid transaction amount: " + line
                                );
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error reading logs: " + e.getMessage());
        }

        // Add final totals
        report.append("\n\n===== SUMMARY =====");
        report.append("\nTotal Deposits: Rs. ").append(totalDeposits);
        report.append("\nTotal Withdrawals: Rs. ").append(totalWithdrawals);
        report.append("\nNet Amount: Rs. ")
                .append(totalDeposits - totalWithdrawals);

        // Save report
        try {
            Files.write(
                    reportFile,
                    report.toString().getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            System.out.println("\nReport generated successfully.");

        } catch (IOException e) {
            System.out.println("Error writing report: " + e.getMessage());
        }
    }
}