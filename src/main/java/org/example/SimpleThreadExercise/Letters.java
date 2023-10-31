package org.example.SimpleThreadExercise;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
class Letters {
    private final List<Thread> threads = new ArrayList<>();
    public Letters(String letters) {

        char[] word = letters.toCharArray();
        for (char c : word) {
            String name = String.valueOf(c);
            Runnable t = () -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        System.out.print(name);
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            };
            threads.add(new Thread(t, "Thread " + name));
        }
    }
}
