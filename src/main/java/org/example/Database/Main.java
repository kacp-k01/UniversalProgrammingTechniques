package org.example.Database;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        File dataDir = new File("data");
        TravelData travelData = new TravelData(dataDir);
        for (String locale : Arrays.asList("pl_PL", "en_GB")) {
            List<String> odlist = travelData.getOffersDescriptionsList(locale);
            for (String od : odlist) System.out.println(od);
        }
        String url = "jdbc:postgresql://localhost:5432/offersdata";
        Database db = new Database(url, travelData);
        db.create();
        db.showGui();
    }

}
