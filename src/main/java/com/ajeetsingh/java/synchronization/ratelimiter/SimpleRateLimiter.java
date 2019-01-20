package com.ajeetsingh.java.synchronization.ratelimiter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * The minimalistic token-bucket implementation
 *
 * Token-bucket algorithm - Throttling method calls to M requests in N seconds
 * To make sure that my method is executed no more than M times in a sliding window of N seconds.
 *
 * Let's say you are exposing a bunch of public RESTFul APIs.
 * You normally want to rate-limit it somehow.
 * That is, to limit the number of requests performed over a period of time,
 * in order to save resources and protect it from abuse.
 * Say for example you want to allow only 60 calls to be made in a 1-minute window.
 * To be able to do this, the famous Token Bucket algorithm comes in to play.
 *
 * Reference - https://github.com/vladimir-bukhtoyarov/bucket4j/blob/2.1/doc-pages/token-bucket-brief-overview.md#example-of-basic-java-token-bucket-implementation
 *              https://konghq.com/blog/how-to-design-a-scalable-rate-limiting-algorithm/
 *
 */
public final class SimpleRateLimiter {
    private final Semaphore _semaphore;
    private final int _maxPermits;
    private final TimeUnit _timePeriod;

    private ScheduledExecutorService _scheduler;

    public static SimpleRateLimiter create(int permits, TimeUnit timePeriod) {
        SimpleRateLimiter limiter = new SimpleRateLimiter(permits, timePeriod);
        limiter.schedulePermitReplenishment();
        return limiter;
    }

    private SimpleRateLimiter(int permits, TimeUnit timePeriod) {
        _semaphore = new Semaphore(permits);
        _maxPermits = permits;
        _timePeriod = timePeriod;
    }

    public boolean tryAcquire() {
        return _semaphore.tryAcquire();
    }

    public void stop() {
        _scheduler.shutdownNow();
    }

    /**
     * To remove expired permits
     */
    public void schedulePermitReplenishment() {
        _scheduler = Executors.newScheduledThreadPool(1);
        _scheduler.scheduleAtFixedRate(() -> {
            _semaphore.release(_maxPermits - _semaphore.availablePermits());
        },0, 1, _timePeriod);
    }
}