package lulewiczg.contentserver.utils.json;

public final class JSONUtil {

    /**
     * Escapes string value
     * 
     * @param value
     *            value
     * @return escaped value
     */
    public static String escape(Object value) {
        return value.toString().replace("\"", "\\\"").replace("\n", "\\n").replace("\t", "\\t");
    }

    /**
     * Transforms object to string. Adds quotes if required.
     * 
     * @param value
     *            value
     * @return string value
     */
    public static String toString(Object value) {
        String result = value.toString();
        result = escape(result);
        if (value instanceof String) {
            result = String.format("\"%s\"", result);
        }
        return result;
    }
}
