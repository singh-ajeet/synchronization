package org.ajeet.learnings.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public final class SimpleThreadPool {
    private final AtomicBoolean isRunning;
    private final ConcurrentLinkedQueue<Runnable> tasksQueue;
    private final List<Runnable> workers;

    public SimpleThreadPool(int capacity){
        if(capacity <= 0)
            throw new IllegalArgumentException("Capacity should be greater than 0.");

        isRunning = new AtomicBoolean(true);
        tasksQueue = new ConcurrentLinkedQueue<Runnable>();
        workers = new ArrayList<>(capacity);

        for(int i=0; i<capacity; i++){
            Worker worker = new Worker( "Worker" + i);
            workers.add(worker);
            worker.start();
        }
    }

    private class Worker extends Thread {
        private final String name;

        private Worker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            //If pool is shutting down and there is no task in queue than stop worker
            while (isRunning.get() || !tasksQueue.isEmpty()) {
                Runnable task;
                while ((task = tasksQueue.poll()) != null){
                    task.run();
                }
            }
          }
    }

    public void execute(Runnable task) throws InterruptedException {
        if(!isRunning.get()){
            throw new InterruptedException("Pool is shutting down.");
        }
        tasksQueue.add(task);
     }

    /**
     * Signal shutdown to running threads
     */
    public void shutdown(){
        isRunning.compareAndSet(true, false);
    }

    /**
     * Shutdown the pool and return missed tasks.
     *
     * @return
     */
    public List<Runnable> shutdownNow(){
        isRunning.compareAndSet(true, false);
        List<Runnable> missedTasks = new ArrayList<>();
        tasksQueue.forEach(task -> missedTasks.add(task));
        tasksQueue.clear();
        return missedTasks;
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleThreadPool pool = new SimpleThreadPool(3);
        try {
            for(int i=1;i<=10;i++){
                pool.execute(() -> {
                    System.out.println("Executing task " + UUID.randomUUID() + " ...");
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
       Thread.sleep(100);
       List<Runnable> missedTasks = pool.shutdownNow();
       System.out.println(missedTasks.size());
    }
}
