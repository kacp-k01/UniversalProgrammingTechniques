package org.example.Database;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
    private final String url;
    private final TravelData travelData;
    private final List<String> resultsArr = new ArrayList<>();

    public Database(String url, TravelData travelData) {
        this.url = url;
        this.travelData = travelData;
    }

    public void create() {
        String uname = "XXXX";
        String password = "XXXX";
        try {
            Connection con = DriverManager.getConnection(url, uname, password);
            Statement statement = con.createStatement();
            String createTableQ = "CREATE TABLE IF NOT EXISTS offers (local VARCHAR(10), country VARCHAR(40), " +
                    "tart_date DATE, end_date DATE, place VARCHAR(30), price VARCHAR(30),currency VARCHAR(10) );";
            statement.executeUpdate(createTableQ);
            File[] flist = travelData.getDataDir().listFiles();
            assert flist != null;
            for (File file : flist) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    List<String> offerLists = new ArrayList<>();
                    String oneOffer = reader.readLine();
                    while (oneOffer != null) {
                        offerLists.add(oneOffer);
                        oneOffer = reader.readLine();
                    }
                    for (String offer : offerLists) {
                        String[] fields = offer.split("\t");
                        String addQ = "INSERT INTO offers VALUES ('" + fields[0] + "', '" + fields[1] + "', TO_DATE('" + fields[2] + "', 'YYYY/MM/DD'), TO_DATE('" +
                                fields[3] + "', 'YYYY/MM/DD'), '" + fields[4] + "', '" + fields[5] + "', '" + fields[6] + "');";
                        statement.executeUpdate(addQ);
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
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
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
        en.addActionListener(e -> {

            f.setVisible(false);
            Locale country = new Locale("en", "GB");

            String[][] data = fillJTable(resultsArr, country);

            String[] column = {"Country", "Start date", "Return date", "Place", "Price", "Currency"};
            JTable jt = new JTable(data, column);
            jt.setBounds(30, 40, 300, 1000);
            JScrollPane sp = new JScrollPane(jt);
            f2.add(sp);
            f2.setVisible(true);
        });

        pl.addActionListener(e -> {
            f.setVisible(false);
            Locale country = new Locale("pl", "PL");

            String[][] data = fillJTable(resultsArr, country);
            String[] column = {"Kraj", "Data wyjazdu", "Data powrotu", "Miejsce", "Cena", "Waluta"};
            JTable jt = new JTable(data, column);
            jt.setBounds(30, 40, 300, 1000);
            JScrollPane sp = new JScrollPane(jt);
            f2.add(sp);
            f2.setVisible(true);
        });
    }

    public String[][] fillJTable(List<String> resultsArr, Locale country) {
        String[][] data = new String[resultsArr.size()][6];

        int index = 0;
        for (String offer : resultsArr) {
            String[] fields = offer.split("\t");

            travelData.translate(fields[0]);
            String money = TravelData.formatNumbers(country, travelData.getLineLocale(), fields);
            if (travelData.getLineLocale().equals(country)) {
                data[index][0] = fields[1];
                data[index][1] = fields[2];
                data[index][2] = fields[3];
                data[index][3] = fields[4];
                data[index][4] = money;
                data[index][5] = fields[6];
            } else {

                travelData.countryNames(travelData.getLineLocale());
            }
            String placeName = travelData.getInfo().getString(fields[4]);
            data[index][0] = travelData.getCountryName();
            data[index][1] = fields[2];
            data[index][2] = fields[3];
            data[index][3] = placeName;
            data[index][4] = money;
            data[index][5] = fields[6];

            index++;
        }
        return data;
    }
}
