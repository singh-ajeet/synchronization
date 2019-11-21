package org.ajeet.learnings.concurrency;

/**
 * A class that provides read and write lock functionality.
 */
public final class ReadWriteBack {
    private int readers = 0;
    private boolean isWriteLocked;

    /**
     * Read lock is allowed if there is no write lock
     *
     * @throws InterruptedException
     */
    public synchronized void acquireReadLock() throws InterruptedException {
        while (isWriteLocked)
            wait();
        readers++;
    }

    /**
     * Only one thread can acquire a write lock
     *
     * @throws InterruptedException
     */
    public synchronized void acquireWriteLock() throws InterruptedException {
        while (isWriteLocked || readers > 0)
            wait();
        isWriteLocked = true;
    }

    /**
     * Release a read lock if there is any
     *
     */
    public synchronized void releaseReadLock(){
        if (readers > 0) {
            readers--;
            notify();
        }
    }

    /**
     * Release a write lock if there is any
     */
    public synchronized void releaseWriteLock(){
        if (isWriteLocked) {
            isWriteLocked = false;
            notify();
        }
    }
}
