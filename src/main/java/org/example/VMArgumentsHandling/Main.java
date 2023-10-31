package org.example.VMArgumentsHandling;

public class Main {

    public static void main(String[] args) {
        Calc c = new Calc();
        String result = c.doCalc(args[0]);
        System.out.println(result);
    }

}
