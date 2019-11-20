package org.ajeet.learnings.concurrency.memory;

public final class MemoryLeak {

    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Before total memory : " + runtime.totalMemory());
        System.out.println("Before free memory : " + runtime.freeMemory());
        for (int i=0; i< 1000_000_000; i++) {
            new Thread(new FinalizeBlocker()).start();
        }
        long after = Runtime.getRuntime().freeMemory();
        //System.gc();
        System.out.println("After total memory : " + runtime.totalMemory());
        System.out.println("After free memory : " + runtime.freeMemory());
    }

    private static final class FinalizeBlocker implements Runnable{
        private static final byte[] MB = new byte[1000000];

        @Override
        protected void finalize() throws Throwable {
           // System.out.println("finalize method of class " + this.getClass().getSimpleName() + " called by garbage collector !!!");
        }

        @Override
        public void run() {
           ThreadLocal< byte[]> threadLocal =  new ThreadLocal<>();
           threadLocal.set(MB);
        }
    }
}

