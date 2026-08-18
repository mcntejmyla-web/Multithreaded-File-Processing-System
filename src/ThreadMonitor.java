public class ThreadMonitor {
    public static void show() {
        System.out.println("\n=== THREAD MONITOR ===");
        System.out.println("Total JVM threads: " + Thread.getAllStackTraces().size());

        for (Thread t : Thread.getAllStackTraces().keySet()) {
            System.out.printf("%-35s %s%n", t.getName(), t.getState());
        }
    }
}
