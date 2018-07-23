package lulewiczg.contentserver.utils.json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents single JSON property.
 *
 * @author Grzegorz
 */
public class JSONPropertyModel {

    private String name;
    private Object value;
    private boolean quoted;

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }

    public boolean isQuoted() {
        return quoted;
    }

    public JSONPropertyModel(String name, Object value, boolean quoted) throws JSONException {
        this.name = name;
        if (value != null) {
            if (value instanceof Collection<?> || value.getClass().isArray()) {
                this.value = processCollection(value);
            } else if (value instanceof JSONModel) {
                this.value = ((JSONModel<?>) value).toJSON();
            } else {
                this.value = JSONUtil.escape(value);
            }
        }
        this.quoted = quoted;
    }

    /**
     * Processes collection.
     * 
     * @param collection
     * @return JSON string
     * @throws JSONException
     *             the JSONException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String processCollection(Object value) throws JSONException {
        List toProcess;
        if (value instanceof Collection<?>) {
            toProcess = new ArrayList<>((Collection<?>) value);
        } else {
            toProcess = new ArrayList<>();
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object object = Array.get(value, i);
                toProcess.add(object);
            }
        }
        if (toProcess.isEmpty()) {
            return "[]";
        } else if (toProcess.get(0) instanceof JSONModel) {
            return JSONModel.toJSONList(toProcess);
        } else {
            return JSONModel.toJSONArray(toProcess);
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
        if (quoted && value != null) {
            return String.format("\n\"%s\": \"%s\"", name, value);
        } else {
            return String.format("\n\"%s\": %s", name, value);
        }
    }
}
