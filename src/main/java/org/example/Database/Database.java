package org.example.Database;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;


public class Database {
    private String url;
    private TravelData travelData;

    private List<String> resultsArr = new ArrayList<>();


    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    public void create() {

        String uname = "postgres";
        String password = "SQLpass1";


        try {

            Connection con = DriverManager.getConnection(url, uname, password);
            Statement statement = con.createStatement();


            String createTableQ = "CREATE TABLE IF NOT EXISTS offers (local VARCHAR(10), country VARCHAR(40), start_date DATE, end_date DATE,"
                    + "place VARCHAR(30), price VARCHAR(30),currency VARCHAR(10) );";
            int result = statement.executeUpdate(createTableQ);


            File[] flist = travelData.getDataDir().listFiles();


            for (File file : flist) {

                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    List<String> offerLists = new ArrayList<String>();
                    String oneOffer = reader.readLine();

                    while (oneOffer != null) {
                        offerLists.add(oneOffer);
                        oneOffer = reader.readLine();
                    }


                    for (String offer : offerLists) {
                        String[] fields = offer.split("\t");

                        String addQ = "INSERT INTO offers VALUES ('" + fields[0] + "', '" + fields[1] + "', TO_DATE('" + fields[2] + "', 'YYYY/MM/DD'), TO_DATE('" +
                                fields[3] + "', 'YYYY/MM/DD'), '" + fields[4] + "', '" + fields[5] + "', '" + fields[6] + "');";

                        int add = statement.executeUpdate(addQ);

                    }

                    String selectAll = "SELECT * from offers;";
                    ResultSet rs = statement.executeQuery(selectAll);

                    while (rs.next()) {
                        String offer = rs.getString("local") + "\t" + rs.getString("country") + "\t" + rs.getString("start_date") + "\t" + rs.getString("end_date")
                                + "\t" + rs.getString("place") + "\t" + rs.getString("price") + "\t" + rs.getString("currency");
                        resultsArr.add(offer);
                    }


                    reader.close();
                } catch (FileNotFoundException e) {
                    System.err.println("Brak pliku tekstowego w katalogu data");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public void showGui() {

        JFrame f = new JFrame();
        JPanel mvPanel = new JPanel();

        JLabel label = new JLabel("Wybierz język / Choose language");
        JButton en = new JButton("EN/GB");
        JButton pl = new JButton("PL");
        mvPanel.setLayout(new BoxLayout(mvPanel, BoxLayout.PAGE_AXIS));
        mvPanel.add(label);
        mvPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mvPanel.add(en);
        mvPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mvPanel.add(pl);
        label.setAlignmentX(JButton.CENTER_ALIGNMENT);
        en.setAlignmentX(JButton.CENTER_ALIGNMENT);
        pl.setAlignmentX(JButton.CENTER_ALIGNMENT);

        f.setLayout(new GridBagLayout());
        f.add(mvPanel, new GridBagConstraints());

        f.setSize(300, 200);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        f.setVisible(true);


        JFrame f2 = new JFrame();
        f2.setSize(1000, 400);
        f2.setLocationRelativeTo(null);
        f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        en.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                f.setVisible(false);
                Locale country = new Locale("en", "GB");


                String data[][] = fillJTable(resultsArr, country);


                String column[] = {"Country", "Start date", "Return date", "Place", "Price", "Currency"};
                JTable jt = new JTable(data, column);
                jt.setBounds(30, 40, 300, 1000);
                JScrollPane sp = new JScrollPane(jt);
                f2.add(sp);


                f2.setVisible(true);


            }
        });


        pl.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                f.setVisible(false);
                Locale country = new Locale("pl", "PL");


                String data[][] = fillJTable(resultsArr, country);


                String column[] = {"Kraj", "Data wyjazdu", "Data powrotu", "Miejsce", "Cena", "Waluta"};
                JTable jt = new JTable(data, column);
                jt.setBounds(30, 40, 300, 1000);
                JScrollPane sp = new JScrollPane(jt);
                f2.add(sp);


                f2.setVisible(true);


            }
        });
    }


    public String[][] fillJTable(List<String> resultsArr, Locale country) {
        String data[][] = new String[resultsArr.size()][6];
        ;

        int index = 0;
        for (String offer : resultsArr) {
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


            DecimalFormat df = (DecimalFormat) NumberFormat.getInstance(country);

            if (lineLocale.equals(new Locale("en", "GB"))) {
                fields[5] = fields[5].replace(",", "");

            } else {
                fields[5] = fields[5].replace(",", ".");
            }
            double mon = Double.parseDouble(fields[5]);

            String money = df.format(mon);

            if (lineLocale.equals(country)) {
                data[index][0] = fields[1];
                data[index][1] = fields[2];
                data[index][2] = fields[3];
                data[index][3] = fields[4];
                data[index][4] = money;
                data[index][5] = fields[6];
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

                data[index][0] = countryName;
                data[index][1] = fields[2];
                data[index][2] = fields[3];
                data[index][3] = placeName;
                data[index][4] = money;
                data[index][5] = fields[6];


            }

            index++;
        }

        return data;
    }
}
