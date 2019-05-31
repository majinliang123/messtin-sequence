package org.messtin.sequence;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) throws InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(5);
        Sequence sequence = new Sequence(null, "abc");

        pool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(sequence.nextId());
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(sequence.nextId());
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(sequence.nextId());
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(sequence.nextId());
            }
        });
        pool.submit(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(sequence.nextId());
            }
        });
        pool.shutdown();
        pool.awaitTermination(100, TimeUnit.DAYS);
        System.out.println("pool is shutdown");
    }
}
