package me.nrubin29.weblang.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public abstract class Provider {

    public abstract Result[] provide(String query) throws IOException;

    String[] read(String u) throws IOException {
        URLConnection c = new URL(u).openConnection();
        c.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        ArrayList<String> lines = new ArrayList<String>();

        while (reader.ready()) {
            lines.add(reader.readLine());
        }

        reader.close();

        return lines.toArray(new String[lines.size()]);
    }
}