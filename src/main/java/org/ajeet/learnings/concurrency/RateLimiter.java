package org.ajeet.learnings.concurrency;

public final class RateLimiter {
    //private static Random random = new Random(System.currentTimeMillis());
    private final int capacity;
    private final long refillPeriod;
    private final int slotsPerMillis;

    private int availableSlots;
    private long previousMillis;

    public RateLimiter(int capacity, long refillPeriod) {
        this.capacity = capacity;
        this.refillPeriod = refillPeriod;
        this.slotsPerMillis = Math.round(capacity/refillPeriod);
        this.availableSlots = capacity;
        this.previousMillis = System.currentTimeMillis();
    }

    public synchronized boolean isSlotAvailable(){
        refill();
        if(availableSlots <= 0)
            return false;
        availableSlots--;
        return true;
    }

    private void refill() {
        long currentMillis = System.currentTimeMillis();
        if (currentMillis > previousMillis){
            int millis = (int) (currentMillis  - previousMillis);
            availableSlots = Math.min(capacity, availableSlots + millis * slotsPerMillis);
            previousMillis = currentMillis;
        }
    }
}
