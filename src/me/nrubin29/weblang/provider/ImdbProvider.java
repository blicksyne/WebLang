package me.nrubin29.weblang.provider;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class ImdbProvider extends Provider {

    @Override
    public Result[] provide(String query) throws IOException {
        String[] lines = read("http://www.imdb.com/xml/find?json=1&nr=1&tt=on&q=" + query.replaceAll(" ", "+"));
        String line = "";

        for (String l : lines) {
            line += l;
        }

        JSONObject object = new JSONObject(line);
        ArrayList<Result> results = new ArrayList<Result>();

        for (String sectionName : object.keySet()) {
            JSONArray section = object.getJSONArray(sectionName);
            for (int i = 0; i < section.length(); i++) {
                JSONObject o = section.getJSONObject(i);
                results.add(new Result(o.getMap()));
            }
        }

        return results.toArray(new Result[results.size()]);
    }
}
