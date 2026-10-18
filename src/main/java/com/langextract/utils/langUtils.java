package com.langextract.utils;

import java.util.Collection;
import java.util.Map;

public class langUtils {
    public boolean isNullOrEmpty(Object object) {
        return switch (object) {
            case null -> true;
            case String s -> s.isEmpty();
            case Collection<?> objects -> objects.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };

    }
}
