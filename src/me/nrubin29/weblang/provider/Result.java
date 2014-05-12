package me.nrubin29.weblang.provider;

import me.nrubin29.weblang.Utils;

import java.util.HashMap;
import java.util.Map;

/*
Represents a result from a search.
Example: In a Google search, each webpage that comes up would be a Result.
 */
public class Result {

    private final Map<Key, Object> data;

    public Result(Provider provider, Map<String, Object> data) throws Utils.InvalidCodeException {
        this.data = new HashMap<Key, Object>();

        for (Map.Entry<String, Object> e : data.entrySet()) {
            this.data.put(provider.getKey(e.getKey()), e.getValue());
        }
    }

    /*
    Get a piece of data given the key.
    Example: Get the URL of a Google search result by passing key "url"
     */
    public String getData(Key key) {
        return data.get(key).toString();
    }

    @Override
    public String toString() {
        String res = "<html>";

        for (Map.Entry<Key, Object> e : data.entrySet()) {
            res += e.getKey() + "=" + e.getValue() + "<br/>";
        }

        return res;
    }
}