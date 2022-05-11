package org.paumard.loom.threads;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class E_HowManyVirtualThreads {

    // --enable-preview

    public static void main(String[] args) throws InterruptedException {

        Set<String> pools = ConcurrentHashMap.newKeySet();
        Set<String> pThreads = ConcurrentHashMap.newKeySet();
        Pattern pool = Pattern.compile("ForkJoinPool-[\\d?]");
        Pattern worker = Pattern.compile("worker-[\\d?]");

        var threads = IntStream.range(0, 10_000)
              .mapToObj(i -> Thread.ofVirtual()
                    .unstarted(() -> {
                        try {
                            Thread.sleep(2_000);
                            String name = Thread.currentThread().toString();
                            Matcher poolMatcher = pool.matcher(name);
                            if (poolMatcher.find()) {
                                pools.add(poolMatcher.group());
                            }
                            Matcher workerMatcher = worker.matcher(name);
                            if (workerMatcher.find()) {
                                pThreads.add(workerMatcher.group());
                            }

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
        System.out.println("# cores = " + Runtime.getRuntime().availableProcessors());
        System.out.println("Time = " + Duration.between(begin, end));
        System.out.println("Pools");
        pools.forEach(System.out::println);
        System.out.println("Platform threads (" + pThreads.size() + ")");
        new TreeSet<>(pThreads).forEach(System.out::println);
    }
}
