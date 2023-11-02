package org.example.Database;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TravelData {

    private File dataDir;
    Locale lineLocale;
    ResourceBundle info;
    String[] fields;
    BufferedReader reader;
    List<String> offers;
    String countryName;
    String placeName;
    String cName;

    public List<String> getOffersDescriptionsList(String locale) {

        String[] locArr = locale.split("_");
        Locale country = new Locale(locArr[0], locArr[1]);
        File[] fileList = dataDir.listFiles();

        assert fileList != null;
        for (File file : fileList) {
            List<String> offerLists = new ArrayList<>();
            try {
                reader = new BufferedReader(new FileReader(file));
                loadFiles(offerLists, reader);

                for (String offer : offerLists) {
                    fields = offer.split("\t");
                    translate(offer);
                    String offerLine;
                    assert false;
                    String money = formatNumbers(country, lineLocale, fields);
                    countryNames(lineLocale);

                    if (lineLocale.equals(country)) {
                        offerLine = fields[1] + " " + fields[2] + " " + fields[3] + " " + fields[4] + " " + money + " " + fields[6];
                        offers.add(offerLine);
                    } else {
                        offerLine = countryName + " " + fields[2] + " " + fields[3] + " " + placeName + " " + money + " " + fields[6];
                        offers.add(offerLine);
                    }
                }

                reader.close();

            } catch (FileNotFoundException e) {
                System.err.println("Brak pliku tekstowego w katalogu data");
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return offers;
    }

    static String formatNumbers(Locale country, Locale lineLocale, String[] fields) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(country);

        if (lineLocale.equals(new Locale("en", "GB"))) {
            fields[5] = fields[5].replace(",", "");

        } else {
            fields[5] = fields[5].replace(",", ".");
        }
        double mon = Double.parseDouble(fields[5]);
        return df.format(mon);
    }

    static void loadFiles(List<String> offerLists, BufferedReader reader) throws IOException {
        String oneOffer = reader.readLine();
        while (oneOffer != null) {
            offerLists.add(oneOffer);
            oneOffer = reader.readLine();
        }
    }

    void translate(String offer) {
        String[] fields = offer.split("\t");
        if (fields[0].contains("pl") || fields[0].contains("PL") || fields[0].contains("Pl")) {
            lineLocale = new Locale("pl", "PL");
            info = ResourceBundle.getBundle("zad1.Translation", lineLocale);

        } else if (fields[0].contains("en") || fields[0].contains("EN") || fields[0].contains("En")) {
            lineLocale = new Locale("en", "GB");
            info = ResourceBundle.getBundle("zad1.Translation", lineLocale);
        } else {
            System.err.println("no locale found");
        }
    }

    void countryNames(Locale country) {
        countryName = "";
        cName = fields[1];

        for (Locale l : Locale.getAvailableLocales()) {
            if (l.getDisplayCountry(lineLocale).equals(cName)) {
                countryName = l.getDisplayCountry(country);
                break;
            }
        }
        placeName = info.getString(fields[4]);
    }
}





