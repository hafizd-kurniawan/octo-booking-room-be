package com.octo.booking_room.utils;

import java.util.UUID;

public class IdGenerator {

    /**
     * Generates a random UUID string without hyphens.
     * Useful for creating database primary key strings (like "cst_...", "book_...").
     *
     * @param prefix Optional prefix to add before the UUID (e.g., "book_", "cst_")
     * @return Generated ID string
     */
    public static String generateId(String prefix) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        if (prefix != null && !prefix.trim().isEmpty()) {
            return prefix + uuid;
        }
        return uuid;
    }

    /**
     * Generates a basic random UUID string.
     *
     * @return Random UUID string
     */
    public static String generateId() {
        return UUID.randomUUID().toString();
    }
}
