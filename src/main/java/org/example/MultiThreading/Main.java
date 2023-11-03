package org.example.MultiThreading;


public class Main {
    public static void main(String[] args) {
        Author author = new Author(args);
        new Thread(author).start();
        new Thread(new Writer(author)).start();
    }
}
