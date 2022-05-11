package org.paumard.loom.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

public class C_MaxPlatformThreads {

    // --enable-preview

    public static void main(String[] args) throws InterruptedException {

        // platform thread
        var threads =
        IntStream.range(0, 100)
              .mapToObj(index ->
                    Thread.ofPlatform()
                          .name("platform-", index)
                          .unstarted(() -> {
                              try {
                                  Thread.sleep(2_000);
                              } catch (InterruptedException e) {
                                  throw new RuntimeException(e);
                              }
                          }))
                    .toList();

        Instant begin = Instant.now();
        threads.forEach(Thread::start);
        for (Thread thread : threads) {
            thread.join();
        }
        Instant end = Instant.now();
        System.out.println("Duration = " + Duration.between(begin, end));
    }
}

