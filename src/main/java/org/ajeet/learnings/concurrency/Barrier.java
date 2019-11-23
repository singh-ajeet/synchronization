package org.ajeet.learnings.concurrency;

public final class Barrier {
    private int count;
    private int totalThread = 0;
    private int released = 0;

    public Barrier(int totalThread) {
        this.totalThread = totalThread;
    }

    public synchronized void await() throws InterruptedException {
        //To block re use of barrier, untill all threads are not succeeded from first run
        while (count == totalThread) {
            wait();
        }

        count++;
        while (count < totalThread) {
            wait();
        }
        if(count == totalThread) {
            notifyAll();
            released = totalThread;
        }
        released --;
        //We need to wait till all threads has been released before resetting count to 0
        //otherwise some threads will stuck in while loop
        if (released == 0)
            count = 0;
    }

    public static void main(String[] args) {
        Barrier barrier = new Barrier(10);

        for(int i = 1; i <= 10; i++) {
            new Thread("Thread" + i) {
                @Override
                public void run() {
                    try {
                        barrier.await();
                        System.out.println(getName() + " running.");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }
}
