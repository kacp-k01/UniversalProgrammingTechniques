package org.example.Database;

import lombok.AllArgsConstructor;
import lombok.Getter;

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
public class TravelData {

    private File dataDir;

    public List<String> getOffersDescriptionsList(String locale) {
        List<String> offers = new ArrayList<>();
        String[] locArr = locale.split("_");
        Locale country = new Locale(locArr[0], locArr[1]);

        File[] fileList = dataDir.listFiles();

        assert fileList != null;
        for (File file : fileList) {
            List<String> offerLists = new ArrayList<>();

            String oneOffer = "";
            try {
                loadFiles(oneOffer, offerLists, file);

                for (String offer : offerLists) {
                    String[] fields = offer.split("\t");
                    Locale lineLocale;
                    ResourceBundle info;

                    if (fields[0].contains("pl") || fields[0].contains("PL") || fields[0].contains("Pl")) {
                        lineLocale = new Locale("pl", "PL");
                        info = ResourceBundle.getBundle("zad1.Translation", lineLocale);

                    } else if (fields[0].contains("en") || fields[0].contains("EN") || fields[0].contains("En")) {
                        lineLocale = new Locale("en", "GB");
                        info = ResourceBundle.getBundle("zad1.Translation", lineLocale);
                    } else {
                        System.err.println("no locale found");
                        continue;
                    }


                    String offerLine = new String();
                    DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(country);

                    if (lineLocale.equals(new Locale("en", "GB"))) {
                        fields[5] = fields[5].replace(",", "");

                    } else {
                        fields[5] = fields[5].replace(",", ".");
                    }
                    double mon = Double.parseDouble(fields[5]);

                    String money = df.format(mon);

                    if (lineLocale.equals(country)) {
                        offerLine = fields[1] + " " + fields[2] + " " + fields[3] + " " + fields[4] + " " + money + " " + fields[6];
                        offers.add(offerLine);
                    } else {

                        String countryName = new String();
                        ;
                        String cName = fields[1];


                        for (Locale l : Locale.getAvailableLocales()) {
                            if (l.getDisplayCountry(lineLocale).equals(cName)) {
                                countryName = l.getDisplayCountry(country);
                                break;
                            }
                        }

                        String placeName = info.getString(fields[4]);

                        offerLine = countryName + " " + fields[2] + " " + fields[3] + " " + placeName + " " + money + " " + fields[6];
                        offers.add(offerLine);
                    }
                }

                reader.close();

            } catch (FileNotFoundException e) {
                System.err.println("Brak pliku tekstowego w katalogu data");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }


        return offers;
    }

    static void loadFiles(String oneOffer, List<String> offerLists, File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        oneOffer = reader.readLine();
        while (oneOffer != null) {
            offerLists.add(oneOffer);
            oneOffer = reader.readLine();
        }
    }

}





