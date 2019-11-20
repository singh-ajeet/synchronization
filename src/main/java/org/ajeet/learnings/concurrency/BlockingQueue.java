package org.ajeet.learnings.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class BlockingQueue<T> {
    private final Object[] buffer;
    private final Lock lock = new ReentrantLock();
    private final Condition emptyCondition = lock.newCondition();
    private final Condition fullCondition = lock.newCondition();

    private int insertIndex = 0;
    private int removeIndex = 0;
    private int count = 0;

    public BlockingQueue(int capacity) {
        this.buffer = new Object[capacity];
    }

    public void get(T element) throws InterruptedException {
        lock.lock();
        try{
            while (isFull()) {
                //Wait if queue is full
                fullCondition.await();
            }
            System.out.println("Inserting here ... " + insertIndex);
           // final Object[] items = this.buffer;
            buffer[insertIndex++] = element;
            if(insertIndex == buffer.length)
                insertIndex = 0;
            count++;
            //Notify waiting threads that new item is ready to consume
            emptyCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    public T get() throws InterruptedException {
        lock.lock();
        try {
            while (isEmpty()) {
                //Wait if no element is available to remove
                emptyCondition.await();
            }
            Object element = buffer[removeIndex];
            buffer[removeIndex++] = null;
            if (removeIndex == buffer.length)
                removeIndex = 0;
            count--;
            //Notify waiting threads that one vacant space is available
            fullCondition.signal();
            return element == null ? null : (T) element;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == buffer.length;
    }

    public static void main(String[] args) {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
       // ArrayBlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        Thread producer = new Thread(() -> {
            try {
                 for(int i =1; i<= 10; i++) {
                    queue.get(i++);
                    System.out.println(i + " was produced.");
                    Thread.currentThread().sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                for(int i =1; i<= 10; i++) {
                    System.out.println(queue.get() + " was consumed.");
                    Thread.currentThread().sleep(1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        producer.start();
        consumer.start();
    }
}
