package org.crawler.service;

import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ExponentialBackoffStrategy {
    int maxBackoff = 3000;
    int maxAttempts = 3;

    @Nullable
    public <T> T attempt(Supplier<T> action, Predicate<T> success) {
        int attempts = 0;

        T result = action.get();

        int k = 0;
        while (!success.test(result) && attempts < maxAttempts) {
            try {
                Thread.sleep(getWaitTimeExp(attempts++));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Failed to get content by url");
            }
            result = action.get();
        }
        return result;
    }

    private long getWaitTimeExp(int attempts) {
        Random rand = new Random();
        final double pow = Math.pow(2, attempts);
        final int random = rand.nextInt(1, 1000);
        return (long) Math.min(pow + random, maxBackoff);
    }
}
