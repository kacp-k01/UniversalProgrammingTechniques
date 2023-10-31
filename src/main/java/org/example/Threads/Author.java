package org.example.Threads;

import lombok.Getter;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ArrayBlockingQueue;

@Getter
public class Author implements Runnable {
    private final String[] input;
    private BlockingQueue<String> queue;

    public Author(String[] input) {
        this.input = input;
        this.queue = new ArrayBlockingQueue<>(input.length);
    }

    @Override
    public void run() {
        try {
            for (String s : this.input) {
                this.queue.put(s);
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

