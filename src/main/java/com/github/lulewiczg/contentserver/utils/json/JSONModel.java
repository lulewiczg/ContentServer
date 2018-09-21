package com.github.lulewiczg.contentserver.utils.json;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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
    public static final String EMPTY_ARR = "[]";
    public static final String EMPTY_OBJ = "{}";

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
        return toJSON(objs, ARR_OPEN_CHAR, ARR_CLOSE_CHAR);
    }

    /**
     * Builds complex type from list of objects.
     * 
     * @param objs
     *            objects
     * @param open
     *            open char
     * @param close
     *            close char
     * @return
     * @throws JSONException
     *             the JSONException
     */
    private static String toJSON(List<?> objs, String open, String close) throws JSONException {
        if (objs.isEmpty()) {
            return open + close;
        }
        StringBuilder builder = new StringBuilder();
        builder.append(open);
        for (Object obj : objs) {
            builder.append(JSONUtil.toString(obj)).append(DELIM);
        }
        deleteLastDelim(builder);
        builder.append(close);
        return builder.toString();
    }

    /**
     * Creates JSON object out of objects
     * 
     * @param objs
     *            objects
     * @return string JSON object
     * @throws JSONException
     *             the JSONException
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static String toJSONObject(List<?> objs) throws JSONException {
        Collections.sort((List<Map.Entry<Comparable, ?>>) objs, new Comparator<Map.Entry<Comparable, ?>>() {
            @Override
            public int compare(Entry<Comparable, ?> o1, Entry<Comparable, ?> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return toJSON(objs, OPEN_CHAR, CLOSE_CHAR);
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
                    serializableProps.add(new JSONPropertyModel(prop.propertyName(), value));
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
