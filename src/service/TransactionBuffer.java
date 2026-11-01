package service;

// Shared array buffer for producer and consumer threads
public class TransactionBuffer {

    private Runnable[] buffer = new Runnable[5];

    private int count = 0;
    private int in = 0;
    private int out = 0;

    private boolean finished = false;

    // Producer adds a transaction
    public synchronized void put(Runnable task)
            throws InterruptedException {

        while (count == buffer.length) {
            wait();
        }

        buffer[in] = task;
        in = (in + 1) % buffer.length;
        count++;

        notifyAll();
    }

    // Consumer takes a transaction
    public synchronized Runnable take()
            throws InterruptedException {

        while (count == 0 && !finished) {
            wait();
        }

        if (count == 0 && finished) {
            return null;
        }

        Runnable task = buffer[out];
        buffer[out] = null;

        out = (out + 1) % buffer.length;
        count--;

        notifyAll();

        return task;
    }

    // Tell the consumer that no more tasks will arrive
    public synchronized void finish() {
        finished = true;
        notifyAll();
    }
}