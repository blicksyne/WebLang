package me.nrubin29.weblang.provider;

import me.nrubin29.weblang.Utils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class WikiaLyricsProvider extends Provider {

    public WikiaLyricsProvider(String arg) {
        super("WikiaLyrics", arg);
    }

    @Override
    public Result[] provide(String query) throws IOException, Utils.InvalidCodeException {
        String[] lines = read("http://lyrics.wikia.com/api.php?func=" + getArg() + "&artist=" + query.replaceAll(" ", "_") + "&fmt=json");
        String line = "";

        for (String l : lines) {
            line += l;
        }

        JSONObject object = new JSONObject(line);
        ArrayList<Result> results = new ArrayList<Result>();

        for (String sectionName : object.keySet()) {
            if (object.get(sectionName) instanceof JSONArray) {
                JSONArray section = object.getJSONArray(sectionName);
                for (int i = 0; i < section.length(); i++) {
                    JSONObject o = section.getJSONObject(i);
                    results.add(new Result(this, o.getMap()));
                }
            }
        }

        return results.toArray(new Result[results.size()]);
    }

    public Key getKey(String str) throws Utils.InvalidCodeException {
        return WikiaLyricsKey.from(str);
    }

    public enum WikiaLyricsKey implements Key {
        AMAZONLINK,
        SONGS,
        ALBUM,
        YEAR;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

        public static Key from(String str) throws Utils.InvalidCodeException {
            for (Key key : values()) {
                if (key.toString().equals(str.toLowerCase())) return key;
            }

            throw new Utils.InvalidCodeException("Attempted to use nonexistent key " + str + ".");
        }
    }
}