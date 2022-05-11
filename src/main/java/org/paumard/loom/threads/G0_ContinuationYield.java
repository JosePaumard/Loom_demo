package org.paumard.loom.threads;

import jdk.internal.vm.Continuation;
import jdk.internal.vm.ContinuationScope;

public class G0_ContinuationYield {

    // --enable-preview --add-exports java.base/jdk.internal.vm=ALL-UNNAMED

    public static void main(String[] args) {

        var scope = new ContinuationScope("hello");
        var continuation =
              new Continuation(
                    scope,
                    () -> {
                        System.out.println("C1: " + Thread.currentThread());
                        Continuation.yield(scope);
                        System.out.println("C2: " + Thread.currentThread());
                    });

        System.out.println("start");
        continuation.run();
        System.out.println("came back");
        continuation.run();
        System.out.println("Done");
    }
}
