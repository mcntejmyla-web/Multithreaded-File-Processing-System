import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ConcurrentFileQueue {
    private final BlockingQueue<Path> queue = new LinkedBlockingQueue<>();

    public void add(Path file) {
        queue.offer(file);
    }

    public Path poll() {
        return queue.poll();
    }

    public int size() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
