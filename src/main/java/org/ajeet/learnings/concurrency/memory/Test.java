package org.ajeet.learnings.concurrency.memory;

public final class Test {
    int[] val = new int[100];
    private static int finalizeCount = 0;

    @Override
    public void finalize() throws Throwable {
        super.finalize();
        finalizeCount++;
    }
    public static void main(String[] args)
    {
        for (int i = 0; i < 1000; i++) { new Test(); }
        System.out.println(
                "Number of times finalize executed: "
                        + Test.finalizeCount);
    }
}
