package me.messageofdeath.commandnpc.Utilities.queue;

import java.util.LinkedList;

public class QueueSystem {

    private volatile boolean stop, interrupt;
    private final QueueExecutor[] threads;
    private final LinkedList<Runnable> queue;

    public QueueSystem(int nThreads) {
        queue = new LinkedList<>();
        stop = false;
        interrupt = false;
        threads = new QueueExecutor[nThreads];
        for(int i = 0; i < nThreads; i++) {
            threads[i] = new QueueExecutor(i);
            threads[i].start();
        }
    }

    public void execute(Runnable r) {
        synchronized (queue) {
            queue.addLast(r);
            queue.notify();
        }
    }

    public synchronized void stop() {
        stop = true;
        if(queue.isEmpty()) {
            interrupt = true;
            for(Thread thread : threads) {
                thread.interrupt();
            }
        }
    }

    private class QueueExecutor extends Thread {

        final int id;

        private QueueExecutor(int id) {
            this.id = id;
        }

        public void run() {
            synchronized (queue) {
                while(!stop || stop && !queue.isEmpty()) {
                    if(queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            if (!interrupt) {
                                e.printStackTrace();
                            }
                            continue;
                        }
                    }
                    try {
                        queue.removeFirst().run();
                    } catch (RuntimeException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
