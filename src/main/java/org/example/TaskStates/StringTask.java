package org.example.TaskStates;

import lombok.Getter;

public class StringTask implements Runnable {
    Thread t;
    private final String str;
    private final int iter;
    @Getter
    private volatile TaskState state;
    @Getter
    private String result = "";
    @Getter
    private boolean done = false;

    public StringTask(String str, int iter) {
        this.str = str;
        this.iter = iter;
        this.state = TaskState.CREATED;
    }

    @Override
    public void run() {
        state = TaskState.RUNNING;
        for (int i = 0; i < iter; i++) {
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            result += str;
        }
        done = true;
        state = TaskState.READY;
    }

    public void start() {
        t = new Thread(this);
        t.start();
    }

    public void abort() {
        done = true;
        t.interrupt();
        state = TaskState.ABORTED;
    }
}
