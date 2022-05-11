package org.paumard.loom.threads;

import jdk.incubator.concurrent.ExtentLocal;
import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class H2_ExtentLocal_ContinuationYield {

    // --enable-preview --add-modules jdk.incubator.concurrent --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) throws InterruptedException, ExecutionException {

        ExtentLocal<Integer> KEY = ExtentLocal.newInstance();

        var continuationScopes =
                IntStream.range(10, 20)
                        .mapToObj(index -> new ContinuationScope("Hello-" + index))
                        .toList();

        Runnable task = () -> {
            int key = KEY.get();
            System.out.println("A-" + key + " [" + Thread.currentThread() + "]");
            Continuation.yield(continuationScopes.get(key - 10));
            System.out.println("B-" + key + " [" + Thread.currentThread() + "]");
            Continuation.yield(continuationScopes.get(key - 10));
            System.out.println("C-" + key + " [" + Thread.currentThread() + "]");
        };

        var continuations =
                IntStream.range(10, 20)
                        .mapToObj(index -> new Continuation(continuationScopes.get(index - 10),
                                () -> ExtentLocal.where(KEY, index).run(task)))
                        .toList();

        var callables =
                continuations.stream()
                        .<Callable<Boolean>>map(continuation -> () -> {
                            continuation.run();
                            return true;
                        })
                        .toList();

        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = executor.invokeAll(callables);
            for (Future<Boolean> future : futures) {
                future.get();
            }
            System.out.println("Step 1");
            futures = executor.invokeAll(callables);
            for (Future<Boolean> future : futures) {
                future.get();
            }
            System.out.println("Step 2");
            futures = executor.invokeAll(callables);
            for (Future<Boolean> future : futures) {
                future.get();
            }
            System.out.println("Step 3");
        }
    }
}
