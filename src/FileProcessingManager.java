import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessingManager {
    private final ThreadPoolExecutor threadPool;
    private final BlockingQueue<Path> fileQueue = new LinkedBlockingQueue<>();
    private final List<Future<FileProcessorTask.Result>> futures = new CopyOnWriteArrayList<>();
    private final AtomicInteger processedFiles = new AtomicInteger();
    private final AtomicInteger failedFiles = new AtomicInteger();
    private long totalProcessingTime;
    private long totalFilesSize;

    public FileProcessingManager(int threadPoolSize) {
        threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadPoolSize);
    }

    public void processDirectory(String directoryPath) throws IOException, InterruptedException {
        System.out.println("\nStarting directory processing: " + directoryPath);

        List<Path> files = new ArrayList<>();
        try (var paths = Files.walk(Paths.get(directoryPath))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> {
                        String n = p.toString().toLowerCase();
                        return n.endsWith(".txt") || n.endsWith(".csv") || n.endsWith(".log");
                    })
                    .forEach(files::add);
        }

        System.out.println("Found " + files.size() + " files to process.");
        if (files.isEmpty()) {
            System.out.println("No .txt, .csv or .log files found.");
            return;
        }

        fileQueue.addAll(files);
        startProcessing();
        awaitCompletion();
        generateReport();
    }

    private void startProcessing() {
        while (!fileQueue.isEmpty()) {
            Path file = fileQueue.poll();
            if (file != null) {
                futures.add(threadPool.submit(new FileProcessorTask(file, processedFiles)));
            }
        }
        System.out.println("Tasks submitted: " + futures.size());
    }

    private void awaitCompletion() throws InterruptedException {
        int total = futures.size();

        while (true) {
            int done = processedFiles.get() + failedFiles.get();
            double percent = total == 0 ? 100 : (done * 100.0 / total);
            System.out.printf("Progress: %d/%d (%.1f%%)%n", done, total, percent);

            if (done >= total) break;
            Thread.sleep(300);
        }

        for (Future<FileProcessorTask.Result> future : futures) {
            try {
                FileProcessorTask.Result r = future.get();
                if (r.success()) {
                    totalProcessingTime += r.processingTimeMs();
                    totalFilesSize += r.sizeBytes();
                } else {
                    failedFiles.incrementAndGet();
                    System.out.println("ERROR: " + r.filename() + " -> " + r.error());
                }
            } catch (ExecutionException e) {
                failedFiles.incrementAndGet();
                System.out.println("Task exception: " + e.getCause());
            }
        }
    }

    private void generateReport() {
        int total = processedFiles.get() + failedFiles.get();
        double successRate = total == 0 ? 0 : processedFiles.get() * 100.0 / total;

        System.out.println("\n=== FILE PROCESSING REPORT ===");
        System.out.println("Total files processed: " + processedFiles.get());
        System.out.println("Failed files: " + failedFiles.get());
        System.out.printf("Success rate: %.1f%%%n", successRate);
        System.out.println("Total processing time: " + totalProcessingTime + " ms");
        System.out.println("Total files size: " + formatFileSize(totalFilesSize));

        if (processedFiles.get() > 0) {
            System.out.println("Average processing time: "
                    + totalProcessingTime / processedFiles.get() + " ms/file");
            System.out.println("Average file size: "
                    + formatFileSize(totalFilesSize / processedFiles.get()));
        }

        System.out.println("\nThread Pool Statistics:");
        System.out.println("Pool size: " + threadPool.getPoolSize());
        System.out.println("Active threads: " + threadPool.getActiveCount());
        System.out.println("Completed tasks: " + threadPool.getCompletedTaskCount());
        System.out.println("Largest pool size: " + threadPool.getLargestPoolSize());
        System.out.println("Queued tasks: " + threadPool.getQueue().size());
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024L * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }

    public void printMonitoring() {
        System.out.println("\n=== THREAD MONITORING DASHBOARD ===");
        System.out.println("Pool size: " + threadPool.getPoolSize());
        System.out.println("Active threads: " + threadPool.getActiveCount());
        System.out.println("Idle threads: " + (threadPool.getPoolSize() - threadPool.getActiveCount()));
        System.out.println("Completed tasks: " + threadPool.getCompletedTaskCount());
        System.out.println("Queued tasks: " + threadPool.getQueue().size());
        System.out.println("Processed files: " + processedFiles.get());
        System.out.println("Failed files: " + failedFiles.get());

        System.out.println("\nAll JVM threads:");
        for (Thread t : Thread.getAllStackTraces().keySet()) {
            System.out.println("- " + t.getName() + " : " + t.getState());
        }
    }

    public void shutdown() {
        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Thread pool shutdown completed.");
    }
}
