package org.usul.plaiground.utils;

import java.lang.reflect.Field;

public class TestUtil {
    public static void setPrivateField(Object targetObject, String fieldName, Object value) {
        try {
            Class<?> clazz = targetObject.getClass();
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);   // allow access to private field
            field.set(targetObject, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed to set field '" + fieldName + "' on object of class " + targetObject.getClass().getName(), e);
        }
    }
}
