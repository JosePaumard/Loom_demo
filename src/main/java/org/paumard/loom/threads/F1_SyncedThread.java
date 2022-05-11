package org.paumard.loom.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

public class F1_SyncedThread {

    private final static Object lock = new Object();
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {

        var threads = IntStream.range(0, 1_000)
              .mapToObj(index -> Thread.ofVirtual()
                    .unstarted(() -> {
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        synchronized (lock) {
                            counter++;
                        }
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        synchronized (lock) {
                            counter++;
                        }
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        synchronized (lock) {
                            counter++;
                        }
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                    }))
              .toList();

        Instant begin = Instant.now();
        for (var thread : threads) {
            thread.start();
        }
        for (var thread : threads) {
            thread.join();
        }
        Instant end = Instant.now();
        synchronized (lock) {
            System.out.println("# counter = " + counter);
        }
        System.out.println("Duration = " + Duration.between(begin, end));

    }
}
