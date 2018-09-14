package lulewiczg.contentserver.utils.json;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class JSONUtil {

    /**
     * Processes collection.
     * 
     * @param collection
     * @return JSON string
     * @throws JSONException
     *             the JSONException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String processCollection(Object value) throws JSONException {
        List toProcess;
        if (value instanceof Collection<?>) {
            toProcess = new ArrayList<>((Collection<?>) value);
        } else if (value instanceof Map<?, ?>) {
            toProcess = new ArrayList<>(((Map<?, ?>) value).entrySet());
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
        } else {
            return JSONModel.toJSONArray(toProcess);
        }
    }

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
     * @param quote
     *            should result be quoted
     * @return string value
     * @throws JSONException
     *             the JSONException
     */
    public static String toString(Object value, boolean quote) throws JSONException {
        if (value instanceof JSONModel<?>) {
            return ((JSONModel<?>) value).toJSON();
        }
        String result = value.toString();
        result = escape(result);
        if (value instanceof String || quote) {
            result = String.format("\"%s\"", result);
        }
        return result;
    }

    /**
     * Transforms object to string. Adds quotes if required.
     * 
     * @param value
     *            value
     * @return string value
     * @throws JSONException
     *             the JSONException
     */
    public static String toString(Object value) throws JSONException {
        return toString(value, false);
    }

    private JSONUtil() {
    }
}
