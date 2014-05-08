package me.nrubin29.weblang.provider;

import java.util.HashMap;
import java.util.Map;

/*
Represents a result from a search.
Example: In a Google search, each webpage that comes up would be a Result.
 */
public class Result {

    private final Map<String, Object> data;

    public Result(Map<String, Object> data) {
        this.data = new HashMap<String, Object>(data);
    }

    /*
    Get a piece of data given the key.
    Example: Get the URL of a Google search result by passing key "url"
     */
    public String getData(String key) {
        return data.get(key).toString();
    }

    @Override
    public String toString() {
        String res = "-----\nResult ";

        for (Map.Entry<String, Object> e : data.entrySet()) {
            res += e.getKey() + "=" + e.getValue() + "\n";
        }

        return res;
    }
}