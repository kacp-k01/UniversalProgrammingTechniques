package org.example.MultiThreading;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Writer implements Runnable {

    private Author author;

    @Override
    public void run() {
        try {
            for (int i = 0; i < this.author.getInput().length; i++) {
                String str = this.author.getQueue().take();
                System.out.println(str);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
