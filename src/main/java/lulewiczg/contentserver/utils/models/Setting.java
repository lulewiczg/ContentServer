package lulewiczg.contentserver.utils.models;

import java.util.List;

/**
 * Model for settings..
 */
public class Setting implements Comparable<Setting> {

    private String name;

    private Object value;

    public Setting(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", name, value);
    }

    /**
     * Generates JSON
     *
     * @param list
     *            permissions
     * @return JSON
     */
    public static String toJSON(List<Setting> list) {
        String s = "[";
        boolean first = true;
        for (Setting p : list) {
            if (!first) {
                s += ",";
            }
            String obj = String.format("{\"name\": \"%s\",\"value\": \"%s\"}", p.name, p.value);
            s += obj;
            first = false;
        }
        s += "]";
        return s;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public int compareTo(Setting o) {
        return name.compareTo(o.name);
    }
}
