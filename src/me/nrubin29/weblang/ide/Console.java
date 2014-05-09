package me.nrubin29.weblang.ide;

import me.nrubin29.weblang.Utils;
import me.nrubin29.weblang.Utils.InvalidCodeException;
import me.nrubin29.weblang.provider.ImdbProvider;
import me.nrubin29.weblang.provider.Key;
import me.nrubin29.weblang.provider.Provider;
import me.nrubin29.weblang.provider.Result;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Console extends JTextPane {

    private boolean waiting = false;
    private String result = null;

    public Console() {
        ((AbstractDocument) getDocument()).setDocumentFilter(new Filter());

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_UP) {
                    e.consume();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (waiting) result = getText().split("\n")[getText().split("\n").length - 1];
                }
            }
        });

        setEditable(false);
        setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 16));
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
                            if (line.split(" ")[1].equals("imdb")) {
                                provider = new ImdbProvider();
                            } else {
                                throw new Utils.InvalidCodeException("Attempted to use nonexistent provider.");
                            }
                        } else if (line.startsWith("query")) {
                            if (provider == null) {
                                throw new Utils.InvalidCodeException("Attempted to query without supplying provider.");
                            }

                            String query = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
                            String varName = line.substring(line.indexOf("storeas ") + "storeas ".length());
                            vars.put(varName, provider.provide(query));
                        } else {
                            for (String varName : vars.keySet()) {
                                if (line.startsWith(varName)) {
                                    String raw = line.substring((varName + "::").length());
                                    String action = raw.substring(0, raw.indexOf("("));
                                    String[] args = raw.substring(raw.indexOf("(") + 1, raw.lastIndexOf(")")).split(" ");

                                    for (int i = 0; i < args.length; i++) {
                                        args[i] = args[i].replaceAll(",", "").trim();
                                    }

                                    if (action.equals("print")) {
                                        write("[[[" + varName + "]]]", MessageType.OUTPUT);
                                        for (Result result : vars.get(varName)) {
                                            write(result.toString(), MessageType.OUTPUT);
                                        }
                                    } else if (action.equals("filter")) {
                                        ArrayList<Result> remove = new ArrayList<Result>();
                                        String act = args[1].substring(0, args[1].indexOf(":")), arg = args[1].substring(args[1].indexOf(":") + 2, args[1].length() - 1);

                                        for (Result r : vars.get(varName)) {
                                            Key k = provider.getKey(args[0]);
                                            if (r.getData(k) != null) {
                                                if (act.equals("contains")) {
                                                    if (!r.getData(k).contains(arg)) {
                                                        remove.add(r);
                                                    }
                                                } else if (act.equals("notcontains")) {
                                                    if (r.getData(k).contains(arg)) {
                                                        remove.add(r);
                                                    }
                                                }
                                            }
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
                }
            }
        }).start();
    }

    public String prompt() {
        setVisible(true);
        getParent().requestFocusInWindow();

        waiting = true;
        setEditable(true);

        while (result == null) {
            try {
                Thread.sleep(100);
            } catch (Exception ignored) {
            }
        }

        waiting = false;
        setEditable(false);

        String localResult = result;
        result = null;
        return localResult;
    }

    public void write(final String txt, final MessageType messageType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    getDocument().insertString(getDocument().getLength(), txt + "\n", messageType.getAttributes());
                } catch (Exception ignored) {
                }

                setCaret();
            }
        });
    }

    private int getLineOfOffset(int offset) throws BadLocationException {
        Document doc = getDocument();
        if (offset < 0) {
            throw new BadLocationException("Can't translate offset to line", -1);
        } else if (offset > doc.getLength()) {
            throw new BadLocationException("Can't translate offset to line", doc.getLength() + 1);
        } else {
            Element map = doc.getDefaultRootElement();
            return map.getElementIndex(offset);
        }
    }

    private int getLineStartOffset(int line) throws BadLocationException {
        Element map = getDocument().getDefaultRootElement();
        if (line < 0) {
            throw new BadLocationException("Negative line", -1);
        } else if (line > map.getElementCount()) {
            throw new BadLocationException("Given line too big", getDocument().getLength() + 1);
        } else {
            Element lineElem = map.getElement(line);
            return lineElem.getStartOffset();
        }
    }

    private void setCaret() {
        try {
            setCaretPosition(getDocument().getLength());
        } catch (Exception ignored) {
        }
    }

    public enum MessageType {
        OUTPUT(Color.BLACK),
        ERROR(Color.RED);

        private final SimpleAttributeSet attributes;

        MessageType(Color color) {
            attributes = new SimpleAttributeSet();
            StyleConstants.setForeground(attributes, color);
        }

        public SimpleAttributeSet getAttributes() {
            return attributes;
        }
    }

    private class Filter extends DocumentFilter {
        @Override
        public void insertString(final FilterBypass fb, final int offset, final String string, final AttributeSet attr)
                throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.insertString(fb, getDocument().getLength(), string, null);
            }
            setCaret();
        }

        @Override
        public void remove(final FilterBypass fb, final int offset, final int length) throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.remove(fb, offset, length);
            }
            setCaret();
        }

        @Override
        public void replace(final FilterBypass fb, final int offset, final int length, final String string, final AttributeSet attrs)
                throws BadLocationException {
            if (getLineStartOffset(getLineOfOffset(offset)) == getLineStartOffset(getLineOfOffset(getDocument().getLength()))) {
                super.replace(fb, offset, length, string, null);
            }
            setCaret();
        }
    }
}