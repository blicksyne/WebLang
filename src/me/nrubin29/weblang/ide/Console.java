package me.nrubin29.weblang.ide;

import me.nrubin29.weblang.Filter;
import me.nrubin29.weblang.Utils;
import me.nrubin29.weblang.Utils.InvalidCodeException;
import me.nrubin29.weblang.provider.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Console extends JPanel {

    public Console() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void run(final String code) {
        new Thread(new Runnable() {
            private Provider provider;
            private HashMap<String, Result[]> vars;

            @Override
            public void run() {
                vars = new HashMap<String, Result[]>();

                try {
                    for (String line : code.split("\n")) {
                        if (line.startsWith("use")) {
                            boolean success = false;

                            if (line.split(" ")[1].equals("imdb")) {
                                provider = new ImdbProvider();
                                success = true;
                            } else if (line.split(" ")[1].equals("wikialyrics")) {
                                provider = new WikiaLyricsProvider(line.split(" ")[2]);
                                success = true;
                            }

                            if (success) {
                                add(new ActionPanel("Selected provider " + provider.getName() + "."));
                            } else {
                                throw new Utils.InvalidCodeException("Attempted to use nonexistent provider.");
                            }
                        } else if (line.startsWith("query")) {
                            if (provider == null) {
                                throw new Utils.InvalidCodeException("Attempted to query without supplying provider.");
                            }

                            String query = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                            String varName = line.substring(line.indexOf("storeas ") + "storeas ".length());

                            add(new ActionPanel("Starting query for \"" + query + "\" and storing to variable " + varName + "."));

                            vars.put(varName, provider.provide(query));

                            add(new ActionPanel("Completed query. Found " + vars.get(varName).length + " results."));
                        } else {
                            for (String varName : vars.keySet()) {
                                if (line.startsWith(varName)) {
                                    String raw = line.substring((varName + "::").length());
                                    String action = raw.substring(0, raw.indexOf("("));
                                    String[] args = raw.substring(raw.indexOf("(") + 1, raw.lastIndexOf(")")).split(", ");

                                    for (int i = 0; i < args.length; i++) {
                                        args[i] = args[i].trim();
                                    }

                                    if (action.equals("print")) {
                                        add(new ActionPanel("Called print. Results:"));

                                        for (Result res : vars.get(varName)) {
                                            add(new ActionPanel(res.toString()));
                                        }
                                    } else if (action.equals("filter")) {
                                        ArrayList<Result> remove = new ArrayList<Result>();
                                        Filter filter = Filter.from(args[1].substring(0, args[1].indexOf(":")));
                                        String arg = args[1].substring(args[1].indexOf(":") + 2, args[1].length() - 1);
                                        Key k = provider.getKey(args[0]);

                                        for (Result r : vars.get(varName)) {
                                            if (r.getData(k) != null) {
                                                if (filter == Filter.CONTAINS) {
                                                    if (!r.getData(k).contains(arg)) {
                                                        remove.add(r);
                                                    }
                                                } else if (filter == Filter.NOTCONTAINS) {
                                                    if (r.getData(k).contains(arg)) {
                                                        remove.add(r);
                                                    }
                                                }
                                            }
                                        }

                                        if (filter == Filter.CONTAINS) {
                                            add(new ActionPanel("Removed all results that did not contain \"" + arg + "\"."));
                                        } else if (filter == Filter.NOTCONTAINS) {
                                            add(new ActionPanel("Removed all results that contained \"" + arg + "\"."));
                                        }

                                        ArrayList<Result> old = new ArrayList<Result>(Arrays.asList(vars.get(varName)));
                                        old.removeAll(remove);
                                        vars.remove(varName);
                                        vars.put(varName, old.toArray(new Result[old.size()]));
                                    } else {
                                        throw new InvalidCodeException("Action " + action + " doesn't exist!");
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
                    add(new ActionPanel("An error has occurred! " + e.getMessage()));
                }
            }
        }).start();
    }

    @Override
    public Component add(Component comp) {
        Component c = super.add(comp);

        getParent().getParent().validate();

        return c;
    }
}