package org.openfact.models.utils;

import java.util.UUID;

public class OpenfactModelUtils {

    public static String generateId() {
        return UUID.randomUUID().toString();
    }

}
