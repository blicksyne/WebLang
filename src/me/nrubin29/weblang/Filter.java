package me.nrubin29.weblang;

public enum Filter {

    CONTAINS, NOTCONTAINS;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public static Filter from(String str) throws Utils.InvalidCodeException {
        for (Filter filter : values()) {
            if (filter.toString().equals(str)) return filter;
        }

        throw new Utils.InvalidCodeException("Attempted to use nonexistent filter " + str + ".");
    }
}