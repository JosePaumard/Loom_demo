package org.paumard.loom.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class F0_SleepingThreads {

    public static void main(String[] args) throws InterruptedException {

        var counter = new AtomicInteger();
        var threads = IntStream.range(0, 1_000)
              .mapToObj(index -> Thread.ofPlatform()
                    .unstarted(() -> {
                        try {
                            if (index == 10) {
                                System.out.println(Thread.currentThread());
                            }
                            Thread.sleep(1_000);
                            counter.incrementAndGet();
                        } catch (InterruptedException e) {
                            throw new AssertionError(e);
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
        System.out.println("# counter = " + counter);
        System.out.println("Duration = " + Duration.between(begin, end));

    }
}
