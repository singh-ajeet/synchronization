package org.ajeet.learnings.concurrency;

public final class RateLimiter {
    private final int capacity;
    private final long refillPeriod;
    private final int tockensPerMillis;

    private int availableTockens;
    private long previousMillis;

    public RateLimiter(int capacity, long refillPeriod) {
        this.capacity = capacity;
        this.refillPeriod = refillPeriod;
        this.tockensPerMillis = Math.round(capacity/refillPeriod);
        this.availableTockens = capacity;
        this.previousMillis = System.currentTimeMillis();
    }

    public synchronized boolean hasToken(){
        refill();
        if(availableTockens <= 0)
            return false;
        availableTockens--;
        return true;
    }

    private void refill() {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis > previousMillis){
            int millis = (int) (currentMillis  - previousMillis);
            availableTockens = Math.min(capacity, availableTockens + millis * tockensPerMillis);
            previousMillis = currentMillis;
        }
    }
}
