package com.langextract.Utils;

import java.util.Collection;
import java.util.Map;

public class LangUtils {
    public static boolean isNullOrEmpty(Object object) {
        return switch (object) {
            case null -> true;
            case String s -> s.isEmpty();
            case Collection<?> objects -> objects.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }

    public static <T> boolean isArray(T obj){
        return !isNullOrEmpty(obj) && obj.getClass().isArray();
    }
}
