package org.paumard.loom.threads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

public class G3_ContinuationYield {

    // --enable-preview --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) throws InterruptedException, ExecutionException {


        var continuationScopes =
              IntStream.range(10, 20)
                    .mapToObj(index -> new ContinuationScope("Hello-" + index))
                    .toList();

        var continuations =
              IntStream.range(10, 20)
                    .mapToObj(index -> new Continuation(continuationScopes.get(index - 10),
                          () -> {
                              System.out.println("A-" + index + " [" + Thread.currentThread() + "]");
                              Continuation.yield(continuationScopes.get(index - 10));
                              System.out.println("B-" + index + " [" + Thread.currentThread() + "]");
                              Continuation.yield(continuationScopes.get(index - 10));
                              System.out.println("C-" + index + " [" + Thread.currentThread() + "]");
                          }))
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
