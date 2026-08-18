# Multithreaded File Processing System

A Java project demonstrating concurrent file processing using `ExecutorService`, `BlockingQueue`, `AtomicInteger`, producer-consumer, thread monitoring, exception handling, progress tracking, and performance benchmarking.

## Requirements

- Java 17 or newer
- IntelliJ IDEA / Eclipse / VS Code

## Run

Compile:

```bash
javac -d out src/*.java
```

Run:

```bash
java -cp out Main
```

## Menu

1. Process Directory - scans `.txt`, `.csv`, `.log` files and processes them with a fixed thread pool.
2. Producer-Consumer - demonstrates a producer and multiple consumers using `BlockingQueue`.
3. Thread Monitoring - displays JVM thread states.
4. Performance Benchmark - compares different thread counts.
5. Exit

## Concepts covered

- Thread pool with `ExecutorService`
- `Callable` and `Future`
- Producer-consumer pattern
- Thread-safe `BlockingQueue`
- Concurrent collection `CopyOnWriteArrayList`
- Atomic counters
- Exception and interruption handling
- Progress tracking
- Thread pool metrics
- Performance measurement

## Quick test

Create a folder containing a few `.txt`, `.csv`, or `.log` files and select option 1.
