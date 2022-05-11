package org.paumard.loom.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

public class F3_LockedThread {

    private final static Lock lock = new ReentrantLock();
    private static int counter = 0;

    public static void main(String[] args) throws InterruptedException {

        var threads = IntStream.range(0, 4_000)
              .mapToObj(index -> Thread.ofVirtual()
                    .unstarted(() -> {
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        lock.lock();
                        try {
                            counter++;
                        } finally {
                            lock.unlock();
                        }
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        lock.lock();
                        try {
                            counter++;
                        } finally {
                            lock.unlock();
                        }
                        if (index == 10) {
                            System.out.println(Thread.currentThread());
                        }
                        lock.lock();
                        try {
                            counter++;
                        } finally {
                            lock.unlock();
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
        lock.lock();
        try {
            System.out.println("# counter = " + counter);
        } finally {
            lock.unlock();
        }
        System.out.println("Duration = " + Duration.between(begin, end));

    }
}
