package org.paumard.loom.threads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class G1_ContinuationYield {

    // --enable-preview --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) throws InterruptedException {


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

        var threads = new ArrayList<Thread>();
        for (Continuation continuation : continuations) {
            threads.add(Thread.ofVirtual().start(continuation::run));
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Step 1");

        threads.clear();
        for (Continuation continuation : continuations) {
            threads.add(Thread.ofVirtual().start(continuation::run));
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Step 2");

        threads.clear();
        for (Continuation continuation : continuations) {
            threads.add(Thread.ofVirtual().start(continuation::run));
        }
        for (Thread thread : threads) {
            thread.join();
        }
        System.out.println("Step 3");
    }
}
