package org.paumard.loom.threads;

public class A_StartingThreads {

    // --enable-preview

    public static void main(String[] args) throws InterruptedException {

        // platform threads
        Thread pthread = new Thread(() -> {
            System.out.println("platform: " + Thread.currentThread());
        });
        pthread.start();
        pthread.join();

        // virtual threads
    }
}
