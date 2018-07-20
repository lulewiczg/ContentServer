package lulewiczg.contentserver.utils.json;

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

    public JSONPropertyModel(String name, Object value, boolean quoted) {
        this.name = name;
        if (value != null) {
            this.value = value.toString().replace("\"", "\\\"");
        }
        this.quoted = quoted;
    }

    @Override
    public String toString() {
        if (quoted && value != null) {
            return String.format("\n\"%s\": \"%s\"\n", name, value);
        } else {
            return String.format("\n\"%s\": %s\n", name, value);
        }
    }
}
