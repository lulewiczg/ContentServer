package lulewiczg.contentserver.utils.models;

import lulewiczg.contentserver.utils.json.JSONModel;
import lulewiczg.contentserver.utils.json.JSONProperty;

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
}
