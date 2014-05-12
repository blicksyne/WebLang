package me.nrubin29.weblang;

import me.nrubin29.weblang.ide.IDE;

import javax.swing.*;

/*
See also:
http://www.omdbapi.com
http://developer.rottentomatoes.com
http://deanclatworthy.com/imdb/
http://imdbapi.poromenos.org
http://www.jmdb.de
 */
class WebLang {

    private static IDE ide;

    public static void main(String[] args) throws Utils.ConsoleException {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                System.out.println("The following stack trace was caught and will be shown to the user:");
                e.printStackTrace();
                if (e.getCause() != null) e.getCause().printStackTrace();

//                if (ide != null && !(e instanceof Utils.IDEException)) {
//                    ide.getConsole().write("Error: " + e, Console.MessageType.ERROR);
//                } else {
//                    JOptionPane.showMessageDialog(null, "Error: " + e, "Error", JOptionPane.ERROR_MESSAGE);
//                }
            }
        });

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ide = new IDE();
            }
        });
    }
}