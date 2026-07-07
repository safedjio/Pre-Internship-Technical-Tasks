package com.safedjio.internship.task6_json;

import com.safedjio.internship.task6_json.annotation.Exclude;
import com.safedjio.internship.task6_json.annotation.JsonName;
import com.safedjio.internship.task6_json.exception.SerializationException;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JsonSerializer {

    private boolean prettyPrint = false;

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public String serialize(Object obj) {
        Set<Integer> visited = new HashSet<>();
        return serializeInternal(obj, visited);
    }

    private String serializeInternal(Object obj, Set<Integer> visited) {
        if (obj == null) {
            return "null";
        }

        Class<?> clazz = obj.getClass();

        if (clazz == String.class || clazz == Character.class) {
            return "\"" + obj.toString().replace("\"", "\\\"") + "\"";
        }

        if (Number.class.isAssignableFrom(clazz) || clazz == Boolean.class || clazz.isPrimitive()) {
            return obj.toString();
        }

        int objectId = System.identityHashCode(obj);
        if (visited.contains(objectId)) {
            throw new SerializationException("Circular reference detected!");
        }
        visited.add(objectId);

        if (Collection.class.isAssignableFrom(clazz)) {
            return serializeCollection((Collection<?>) obj, visited);
        }

        if (clazz.isArray()) {
            return serializeArray(obj, visited);
        }

        return serializeObject(obj, clazz, visited);
    }

    private String serializeCollection(Collection<?> collection, Set<Integer> visited) {
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object item : collection) {
            if (!first) sb.append(",");
            sb.append(serializeInternal(item, visited));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeArray(Object array, Set<Integer> visited) {
        StringBuilder sb = new StringBuilder("[");
        int length = Array.getLength(array);
        for (int i = 0; i < length; i++) {
            if (i > 0) sb.append(",");
            Object item = Array.get(array, i);
            sb.append(serializeInternal(item, visited));
        }
        sb.append("]");
        return sb.toString();
    }

    private String serializeObject(Object obj, Class<?> clazz, Set<Integer> visited) {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) || Modifier.isTransient(field.getModifiers())) {
                continue;
            }

            if (field.isAnnotationPresent(Exclude.class)) {
                continue;
            }

            field.setAccessible(true);

            try {
                Object value = field.get(obj);
                if (value == null) continue;

                if (!first) sb.append(",");

                String keyName = field.getName();
                if (field.isAnnotationPresent(JsonName.class)) {
                    keyName = field.getAnnotation(JsonName.class).value();
                }

                sb.append("\"").append(keyName).append("\":");

                sb.append(serializeInternal(value, visited));

                first = false;

            } catch (IllegalAccessException e) {
                throw new SerializationException("Failed to access field: " + field.getName());
            }
        }

        sb.append("}");
        visited.remove(System.identityHashCode(obj));
        return sb.toString();
    }
}