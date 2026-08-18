import java.nio.file.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n=== MULTITHREADED FILE PROCESSING SYSTEM ===");
            System.out.println("1. Process Directory (Thread Pool)");
            System.out.println("2. Producer-Consumer Example");
            System.out.println("3. Thread Monitoring");
            System.out.println("4. Performance Benchmark");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("Enter directory path: ");
                        String path = sc.nextLine().trim();

                        System.out.print("Enter thread pool size (4-8 recommended): ");
                        int threads = Integer.parseInt(sc.nextLine().trim());
                        threads = Math.max(1, threads);

                        FileProcessingManager manager = new FileProcessingManager(threads);
                        try {
                            manager.processDirectory(path);
                            manager.printMonitoring();
                        } finally {
                            manager.shutdown();
                        }
                    }
                    case "2" -> new ProducerConsumerExample().runExample();
                    case "3" -> ThreadMonitor.show();
                    case "4" -> new PerformanceBenchmark().runBenchmark();
                    case "5" -> {
                        System.out.println("Goodbye!");
                        sc.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Enter 1-5.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
