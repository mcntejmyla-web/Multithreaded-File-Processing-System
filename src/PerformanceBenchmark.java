import java.util.concurrent.*;

public class PerformanceBenchmark {
    public void runBenchmark() throws InterruptedException {
        System.out.println("\n=== PERFORMANCE BENCHMARK ===");
        System.out.println("Threads | Simulated Time | Speedup");

        long single = runTest(1);
        int[] sizes = {1, 2, 4, 8};

        for (int size : sizes) {
            long time = runTest(size);
            double speedup = (double) single / time;
            System.out.printf("%7d | %15d ms | %.2fx%n", size, time, speedup);
        }

        System.out.println("\nRecommended: 4-8 threads for this demo workload.");
    }

    private long runTest(int threads) throws InterruptedException {
        long start = System.currentTimeMillis();
        ExecutorService pool = Executors.newFixedThreadPool(threads);

        for (int i = 0; i < 20; i++) {
            pool.submit(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        pool.shutdown();
        pool.awaitTermination(10, TimeUnit.SECONDS);
        return System.currentTimeMillis() - start;
    }
}
