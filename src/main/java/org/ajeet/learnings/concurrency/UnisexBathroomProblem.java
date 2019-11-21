package org.ajeet.learnings.concurrency;

import java.util.concurrent.Semaphore;

public final class UnisexBathroomProblem {
    private final Semaphore BATHROOM_CAPACITY = new Semaphore(3);
    //Count is required to reset inside gender to 0.
    private int insideCount;
    private Gender insideGender;

    public void maleUseBathroom() throws InterruptedException {
        synchronized (this) {
            while(insideGender == Gender.Female)
                wait();
            BATHROOM_CAPACITY.acquire();
            //insideCount++;
            insideGender = Gender.Male;
        }
        System.out.println("Male is using bathroom ...");
        Thread.sleep(1000); //access bathroom
        BATHROOM_CAPACITY.release();

        synchronized (this) {
            //insideCount--;
            if(insideCount == 0)
                insideGender = null;
            this.notifyAll();
        }
    }

    public void femaleUseBathroom() throws InterruptedException {
        synchronized (this) {
            while(insideGender == Gender.Male)
                wait();
            BATHROOM_CAPACITY.acquire();
            insideCount++;
            insideGender = Gender.Female;
        }
        System.out.println("Female is using bathroom ...");
        Thread.sleep(1000); //access bathroom
        BATHROOM_CAPACITY.release();

        synchronized (this) {
            insideCount--;
            if(insideCount == 0)
                insideGender = null;
            this.notifyAll();
        }
    }

    private static enum Gender {
        Male,
        Female
    }
}
