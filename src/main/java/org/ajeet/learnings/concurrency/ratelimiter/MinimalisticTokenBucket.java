package org.ajeet.learnings.concurrency.ratelimiter;

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
 */
public class MinimalisticTokenBucket {
    private final long capacity;
    private final double refillTokensPerOneMillis;

    private double availableTokens;
    private long lastRefillTimestamp;

    /**
     * Creates token-bucket with specified capacity and refill rate equals to refillTokens/refillPeriodMillis
     */
    public MinimalisticTokenBucket(long capacity, long refillTokens, long refillPeriodMillis) {
        this.capacity = capacity;
        this.refillTokensPerOneMillis = (double) refillTokens / (double) refillPeriodMillis;

        this.availableTokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    synchronized public boolean tryConsume(int numberTokens) {
        refill();
        if (availableTokens < numberTokens) {
            return false;
        } else {
            availableTokens -= numberTokens;
            return true;
        }
    }

    private void refill() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis > lastRefillTimestamp) {
            long millisSinceLastRefill = currentTimeMillis - lastRefillTimestamp;
            double refill = millisSinceLastRefill * refillTokensPerOneMillis;
            this.availableTokens = Math.min(capacity, availableTokens + refill);
            this.lastRefillTimestamp = currentTimeMillis;
        }
    }

    private static final class Selftest {

        public static void main(String[] args) {
            // 100 tokens per 1 second
            MinimalisticTokenBucket limiter = new MinimalisticTokenBucket(100, 100, 1000);

            long startMillis = System.currentTimeMillis();
            long consumed = 0;
            while (System.currentTimeMillis() - startMillis < 10000) {
                if (limiter.tryConsume(1)) {
                    consumed++;
                }
            }
            System.out.println(consumed);
        }

    }
}