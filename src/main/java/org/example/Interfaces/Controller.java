package org.example.Interfaces;

import org.example.Interfaces.models.Bind;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class Controller {
    private final String modelName;
    private Object model;
    private Class<?> modelClass;
    private final Map<String, double[]> extraData = new HashMap<>();
    private String[] years;
    Field[] fields;
    int LLtemp = 0;
    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("groovy");
    public Bindings bindings = engine.getBindings(ScriptContext.ENGINE_SCOPE);

    public Controller(String modelName) {
        this.modelName = modelName;

        try {
            modelClass = Class.forName("zad1.models." + modelName);
            this.model = modelClass.newInstance();

        } catch (InstantiationException e) {
            System.err.println("Nie można utworzyć obiektu klasy " + "zad1.models." + modelName);
        } catch (IllegalAccessException e) {
            System.err.println("Brak definicji klasy " + "zad1.models." + modelName);
        } catch (ClassNotFoundException e) {
            System.err.println("Nie ma takiego modelu");
        }
    }

    public Controller readDataFrom(String fname) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fname));
            fields = modelClass.getDeclaredFields();
            int firstL = 0;
            String line = reader.readLine();
            while (line != null) {
                if (firstL == 0) {
                    try {
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Bind.class) && field.getName().equals("LL")) {
                                field.setAccessible(true);

                                String[] lata = line.split("\\s+");
                                this.years = new String[lata.length - 1];
                                System.arraycopy(lata, 1, this.years, 0, lata.length - 1);
                                LLtemp = lata.length - 1;
                                field.set(model, LLtemp);
                            }
                        }
                        firstL++;

                    } catch (IllegalAccessException e) {
                        System.err.println(e.getMessage());
                    }
                } else {
                    String[] variable = line.split("\\s+");
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(Bind.class) && field.getName().equals(variable[0])) {
                            try {
                                field.setAccessible(true);
                                double[] temp = new double[LLtemp];

                                for (int i = 0; i < LLtemp; i++) {
                                    if (i < (variable.length - 1)) {
                                        temp[i] = Double.parseDouble(variable[i + 1]);
                                    } else {
                                        temp[i] = temp[i - 1];
                                    }
                                }
                                field.set(model, temp);
                            } catch (IllegalAccessException e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }
                }
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.err.println("Brak pliku tekstowego w katalogu data");
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return this;
    }

    public Controller runModel() {
        try {
            Method method = modelClass.getMethod("run");
            method.setAccessible(true);
            method.invoke(model);
        } catch (NoSuchMethodException e) {
            System.err.println("Brak podanej metody z klasy " + "zad1.models." + modelName);
            System.err.println(e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Niepoprawny argument dla metody z klasy " + "zad1.models." + modelName);
            System.err.println(e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println(e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Brak definicji klasy " + "zad1.models." + modelName);
            System.err.println(e.getMessage());
        }
        return this;
    }

    public void runScriptFromFile(String fname) {
        try {
            File file = new File(fname);
            Scanner scanner = new Scanner(file);
            StringBuilder stringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                stringBuilder.append(scanner.nextLine()).append("\n");
            }
            String script = stringBuilder.toString();
            scanner.close();

            for (Field field : fields) {
                if (field.isAnnotationPresent(Bind.class) && field.getName().equals("LL")) {
                    field.setAccessible(true);
                    int val = (int) field.get(model);
                    bindings.put(field.getName(), val);
                } else if (field.isAnnotationPresent(Bind.class)) {
                    field.setAccessible(true);
                    double[] val = (double[]) field.get(model);
                    bindings.put(field.getName(), val);
                }
            }
            String[] newVar = script.split("\\s+", 2);
            engine.eval(script, bindings);
            double[] val = (double[]) bindings.get(newVar[0]);
            bindings.put(newVar[0], val);
            extraData.put(newVar[0], val);
        } catch (IllegalAccessException e) {
            System.err.println("Brak do definicj z klasy " + "zad1.models." + modelName);
            System.err.println(e.getMessage());
        } catch (IOException e) {
            System.err.println("problem z plikiem " + fname);
            System.err.println(e.getMessage());
        } catch (ScriptException e) {
            System.err.println("problem ze skryptem z pliku" + fname);
            System.err.println(e.getMessage());
        }
    }

    public String getResultsAsTsv() {
        String res = "";
        for (Field field : fields) {
            try {
                if (field.isAnnotationPresent(Bind.class) && field.getName().equals("LL")) {
                    field.setAccessible(true);
                    res = "LATA";
                    for (String year : years) {
                        res = res + '\t' + year;
                    }
                    res = res + '\n';
                } else if (field.isAnnotationPresent(Bind.class)) {
                    field.setAccessible(true);
                    res += field.getName();
                    double[] val = (double[]) field.get(model);
                    for (double v : val) {
                        res += '\t' + v;
                    }
                    res = res + '\n';
                }
            } catch (IllegalAccessException e) {
                System.err.println("Brak do definicj z klasy " + "zad1.models." + modelName);
                System.err.println(e.getMessage());
            }
        }
        for (String key : extraData.keySet()) {
            double[] values = extraData.get(key);
            res += key;
            for (double value : values) {
                res += '\t' + value;
            }
            res += '\n';
        }
        return res;
    }
}
