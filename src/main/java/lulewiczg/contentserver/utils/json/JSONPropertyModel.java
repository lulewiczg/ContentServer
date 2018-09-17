package lulewiczg.contentserver.utils.json;

import java.util.Collection;
import java.util.Map;

/**
 * Represents single JSON property.
 *
 * @author Grzegorz
 */
public class JSONPropertyModel {

    private String name;
    private Object value;

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public JSONPropertyModel(String name, Object value) throws JSONException {
        this.name = name;
        if (value != null) {
            if (value instanceof Collection<?> || value instanceof Map<?, ?> || value.getClass().isArray()) {
                this.value = JSONUtil.processCollection(value);
            } else {
                this.value = JSONUtil.toString(value, false);
            }
        }
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof JSONPropertyModel)) {
            return false;
        }
        return name != null && name.equals(((JSONPropertyModel) obj).name);
    }

    @Override
    public String toString() {
        return String.format("%n\"%s\": %s", name, value);
    }
}
