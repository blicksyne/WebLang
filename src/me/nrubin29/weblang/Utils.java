package me.nrubin29.weblang;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class Utils {

//    public static String implode(String s, Block block) throws InvalidCodeException {
//        StringBuilder builder = new StringBuilder();
//
//        boolean inQuotes = false;
//
//        for (String str : s.split(" ")) {
//            if (str.startsWith("\"")) inQuotes = true;
//
//            if (inQuotes) builder.append(str.replaceAll("\"", ""));
//
//            else {
//                if (block != null) {
//                    try {
//                        Variable v = block.getVariable(str.contains(":") ? str.substring(0, str.lastIndexOf(":")) : str);
//                        if (v.isArray()) {
//                            if (str.contains(":"))
//                                builder.append(v.getValue(Integer.parseInt(implode(str.substring(str.lastIndexOf(":") + 1), block))));
//                            else builder.append(Arrays.toString(v.getValues()));
//                        } else builder.append(v.getValue());
//                    } catch (InvalidCodeException e) {
//                        if (e.getMessage().equals("Index does not exist.")) throw e;
//                        builder.append(str);
//                    }
//                }
//            }
//
//            builder.append(" ");
//
//            if (str.endsWith("\"")) inQuotes = false;
//        }
//
//        return builder.toString().trim();
//    }
//
//    public static Object[] implode(String[] s, Block block) throws InvalidCodeException {
//        Object[] objs = new Object[s.length];
//
//        for (int i = 0; i < objs.length; i++) {
//            objs[i] = implode(s[i], block);
//        }
//
//        return objs;
//    }

    public static String changeCommas(String commaStr) {
        StringBuilder builder = new StringBuilder();

        boolean inQuotes = false;

        for (String str : commaStr.split(" ")) {
            for (char c : str.toCharArray()) {
                if (c == ' ') {
                    builder.append(" ");
                } else if (c == '"') {
                    inQuotes = !inQuotes;
                    builder.append("\"");
                } else if (c == ',') {
                    if (inQuotes) builder.append("__comma__");
                    else builder.append(",");
                } else {
                    builder.append(c);
                }
            }
            builder.append(" ");
        }

        return builder.toString().trim();
    }

    public static String unchangeCommas(String commaStr) {
        return commaStr.replaceAll("__comma__", ",").trim();
    }

    public static Object[] replace(Object[] array, Object old, Object n) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(old)) array[i] = n;
        }

        return array;
    }

    private static String[] readFile(File file) {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new FileReader(file));

            while (reader.ready()) {
                builder.append(reader.readLine()).append("\n");
            }

            reader.close();

            return builder.toString().trim().split("\n");
        } catch (Exception ex) {
            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
            return null;
        }
    }

    public static String readFile(File file, String delimiter) {
        StringBuilder builder = new StringBuilder();

        for (String line : readFile(file)) {
            builder.append(line).append(delimiter);
        }

        return builder.toString().trim();
    }

    static class ConsoleException extends Exception {
        public ConsoleException(String msg) {
            super(msg);
        }
    }

    public static class IDEException extends Exception {
        public IDEException(String msg) {
            super(msg);
        }
    }

    public static class InvalidCodeException extends Exception {
        public InvalidCodeException(String msg) {
            super(msg);
        }
    }
}
