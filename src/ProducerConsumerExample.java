import java.util.*;
import java.util.concurrent.*;

public class ProducerConsumerExample {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>(10);
    private static final String END = "__END__";

    public void runExample() throws InterruptedException {
        List<String> files = Arrays.asList(
                "file1.txt", "file2.txt", "file3.txt", "file4.txt",
                "file5.txt", "file6.txt", "file7.txt", "file8.txt"
        );

        Thread producer = new Thread(() -> {
            try {
                for (String file : files) {
                    queue.put(file);
                    System.out.println("Producer added: " + file +
                            " (Queue size: " + queue.size() + ")");
                }
                queue.put(END);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Producer");

        Runnable consumerWork = () -> {
            int count = 0;
            try {
                while (true) {
                    String file = queue.take();
                    if (END.equals(file)) {
                        queue.put(END);
                        break;
                    }
                    System.out.println(Thread.currentThread().getName()
                            + " processing: " + file);
                    Thread.sleep(100);
                    count++;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName()
                    + " processed " + count + " files");
        };

        Thread c1 = new Thread(consumerWork, "Consumer-1");
        Thread c2 = new Thread(consumerWork, "Consumer-2");
        Thread c3 = new Thread(consumerWork, "Consumer-3");

        producer.start();
        c1.start();
        c2.start();
        c3.start();

        producer.join();
        c1.join();
        c2.join();
        c3.join();

        System.out.println("\nProducer-Consumer example completed!");
        System.out.println("Queue maximum capacity: 10");
        System.out.println("Thread-safe queue: LinkedBlockingQueue");
    }
}
