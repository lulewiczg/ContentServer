package com.github.lulewiczg.contentserver.utils.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.github.lulewiczg.contentserver.utils.Constants;
import com.github.lulewiczg.contentserver.utils.json.JSONModel;
import com.github.lulewiczg.contentserver.utils.json.JSONProperty;

/**
 * Model for settings.
 */
public class Setting extends JSONModel<Setting> implements Comparable<Setting> {

    @JSONProperty(propertyName = "name")
    private String name;

    @JSONProperty(propertyName = "value")
    private Object value;

    public Setting(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("%s = %s", name, value);
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Setting)) {
            return false;
        }
        return name.equals(((Setting) obj).name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Parses settings to save.
     * 
     * @param data
     *            data to save
     * @return parsed settings
     */
    public static List<Setting> load(Map<String, Object> data) {
        // Maybe change this later?
        List<Setting> settings = new ArrayList<>();
        Set<Entry<String, Object>> entrySet = data.entrySet();
        for (Map.Entry<String, Object> e : entrySet) {
            String key = e.getKey();
            String value;
            if (e.getValue().getClass().isArray()) {
                value = ((String[]) e.getValue())[0];
            } else {
                value = e.getValue().toString();
            }
            Setting parsed;
            if (key.equals(Constants.Setting.BUFFER_SIZE)) {
                value = parseBuffSize(value);
            } else if (key.equals(Constants.Setting.LOGGER_LEVEL)) {
                value = Level.parse(value).toString();
            }
            parsed = new Setting(key, value);
            settings.add(parsed);
        }
        return settings;
    }

    /**
     * Parses buffer size.
     * 
     * @param value
     *            value
     * @return parsed value
     */
    private static String parseBuffSize(String value) {
        int val;
        try {
            val = Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(value + " is not valid buffer size!");
        }
        if (val < 1) {
            throw new IllegalArgumentException(value + " is not valid buffer size!");
        }
        return val + "";
    }
}
