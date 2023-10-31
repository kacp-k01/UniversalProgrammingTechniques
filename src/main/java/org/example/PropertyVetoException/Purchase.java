package org.example.PropertyVetoException;

import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.VetoableChangeSupport;

@Getter
@Setter
public class Purchase {
    private String prod;
    private String data;
    private Double price;

    //	extra
    private final PropertyChangeSupport chg = new PropertyChangeSupport(this);
    private final VetoableChangeSupport veto = new VetoableChangeSupport(this);


    public Purchase(String prod, String data, Double price) {
        this.prod = prod;
        this.data = data;
        this.price = price;
    }

    public synchronized void addPropertyChangeListener(PropertyChangeListener l) {
        chg.addPropertyChangeListener(l);
    }

    public synchronized void addVetoableChangeListener(VetoableChangeListener l) {
        veto.addVetoableChangeListener(l);
    }


    synchronized void setData(String newTxt) {
        String oldTxt = this.data;
        this.data = newTxt;
        chg.firePropertyChange("data", oldTxt, newTxt);
        System.out.println("Change value of: data from: " + oldTxt + " to: " + newTxt);
    }

    synchronized void setPrice(Double newPrc) throws PropertyVetoException {
        Double oldPrc = this.price;
        veto.fireVetoableChange("price", oldPrc, newPrc);
        if (newPrc < 1000) {
            throw new PropertyVetoException("Price change to: " + newPrc + " not allowed", null);
        } else {
            this.price = newPrc;
            System.out.println("Change value of: price from: " + oldPrc + " to: " + newPrc);
        }
    }

    @Override
    public String toString() {
        return "Purchase [" +
                "prod=" + prod +
                ", data=" + data +
                ", price=" + price +
                ']';
    }
}