package org.example.VMArgumentsHandling;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;

public class Calc {

    public String doCalc(String exp) {

        BigDecimal res;
        try {
            String[] tab = exp.split("\\s+");
            BigDecimal l1 = new BigDecimal(tab[0]);
            BigDecimal l2 = new BigDecimal(tab[2]);
            HashMap<String, BigDecimal> bigDecOp = new HashMap<>();
            bigDecOp.put("+", l1.add(l2));
            bigDecOp.put("-", l1.subtract(l2));
            bigDecOp.put("*", l1.multiply(l2));
            bigDecOp.put("/", l1.divide(l2, MathContext.DECIMAL64));
            res = bigDecOp.get(tab[1]);
            return res.toPlainString();

        } catch (Exception e) {
            return "Invalid command to calc";
        }


    }

}  
