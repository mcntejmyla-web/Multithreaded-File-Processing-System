import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;

public class FileProcessorTask implements Callable<FileProcessorTask.Result> {
    private final Path filePath;
    private final AtomicInteger processedCount;

    public FileProcessorTask(Path filePath, AtomicInteger processedCount) {
        this.filePath = filePath;
        this.processedCount = processedCount;
    }

    @Override
    public Result call() {
        long start = System.currentTimeMillis();
        String thread = Thread.currentThread().getName();

        try {
            long size = Files.size(filePath);
            List<String> lines = Files.readAllLines(filePath);

            long words = lines.stream()
                    .flatMap(line -> Arrays.stream(line.trim().split("\\s+")))
                    .filter(w -> !w.isEmpty())
                    .count();

            int number = processedCount.incrementAndGet();
            return new Result(
                    filePath.getFileName().toString(),
                    thread,
                    size,
                    lines.size(),
                    words,
                    System.currentTimeMillis() - start,
                    true,
                    null,
                    number
            );
        } catch (Exception e) {
            return new Result(
                    filePath.getFileName().toString(),
                    thread,
                    0,
                    0,
                    0,
                    System.currentTimeMillis() - start,
                    false,
                    e.getMessage(),
                    0
            );
        }
    }

    public record Result(
            String filename,
            String thread,
            long sizeBytes,
            long lineCount,
            long wordCount,
            long processingTimeMs,
            boolean success,
            String error,
            int processedNumber
    ) {}
}
