package org.paumard.loom.threads;

public class B_ThreadBuilders {

    // --enable-preview

    public static void main(String[] args) throws InterruptedException {
        // platform thread
        var pthread = Thread.ofPlatform()
                .name("platform-", 0)
                .start(() -> {
                    System.out.println("platform " + Thread.currentThread());
                });
        pthread.join();

        // virtual thread
        var vthread = Thread.ofVirtual()
                .name("virtual-", 0)
                .start(() -> {
                    System.out.println("virtual " + Thread.currentThread());
                });
        vthread.join();
    }
}
