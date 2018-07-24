package lulewiczg.contentserver.utils.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Abstract model represent JSON object. To add field to JSON result, use
 * {@link JSONProperty}.
 *
 * @author Grzegorz
 */
public abstract class JSONModel<T> {

    private static final String ARR_CLOSE_CHAR = "]";
    private static final String ARR_OPEN_CHAR = "[";
    private static final String DELIM = ",";
    private static final String CLOSE_CHAR = "}";
    private static final String OPEN_CHAR = "{";

    /**
     * Creates JSON array out of objects
     * 
     * @param objs
     *            objects
     * @return string JSON array
     * @throws JSONException
     *             the JSONException
     */
    public static String toJSONArray(List<?> objs) throws JSONException {
        StringBuilder builder = new StringBuilder();
        builder.append(ARR_OPEN_CHAR);
        for (Object obj : objs) {
            builder.append(JSONUtil.toString(obj)).append(DELIM);
        }
        deleteLastDelim(builder);
        builder.append(ARR_CLOSE_CHAR);
        return builder.toString();
    }

    /**
     * Deletes last delimiter if necessary.
     *
     * @param builder
     *            builder
     */
    private static void deleteLastDelim(StringBuilder builder) {
        int length = builder.length();
        if (length > 1) {
            builder.delete(length - 1, length);
        }
    }

    public String toJSON() throws JSONException {
        Field[] declaredFields = getClass().getDeclaredFields();
        List<JSONPropertyModel> fieldsToSerialize = getFieldsToSerialize(declaredFields);
        if (new HashSet<>(fieldsToSerialize).size() != fieldsToSerialize.size()) {
            throw new JSONException("Duplicated fields found!");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(OPEN_CHAR);
        for (JSONPropertyModel prop : fieldsToSerialize) {
            builder.append(prop.toString()).append(DELIM);
        }
        deleteLastDelim(builder);
        builder.append("\n").append(CLOSE_CHAR);
        return builder.toString();
    }

    private List<JSONPropertyModel> getFieldsToSerialize(Field[] declaredFields) throws JSONException {
        List<JSONPropertyModel> serializableProps = new ArrayList<>();
        for (Field f : declaredFields) {
            JSONProperty prop = getJSONAnnotation(f);
            if (prop != null) {
                try {
                    Method declaredMethod = getClass().getDeclaredMethod(buildGetterName(f));
                    Object value = declaredMethod.invoke(this);
                    serializableProps.add(new JSONPropertyModel(prop.propertyName(), value,
                            value instanceof String || prop.quoted()));
                } catch (ReflectiveOperationException e) {
                    throw new JSONException(e);
                }
            }
        }
        return serializableProps;
    }

    /**
     * Builds getter name.
     *
     * @param f
     *            field
     * @return getter name
     */
    private String buildGetterName(Field f) {
        String prefix = f.getType() == Boolean.class || f.getType() == boolean.class ? "is" : "get";
        String name = f.getName();
        return prefix + name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
    }

    /**
     * Gets {@link JSONProperty} annotation from field if present.
     *
     * @param f
     *            Field
     * @return annotation if present
     */
    private JSONProperty getJSONAnnotation(Field f) {
        Annotation[] annotations = f.getAnnotations();
        if (annotations == null) {
            return null;
        }
        JSONProperty prop = null;
        for (Annotation a : annotations) {
            if (a instanceof JSONProperty) {
                prop = (JSONProperty) a;
                break;
            }
        }
        return prop;
    }

}
