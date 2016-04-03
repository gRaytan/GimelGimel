package com.teamagam.gimelgimel.app.utils;

/**
 * Utility class for managing unique IDs for entities and layers
 * throughout the application lifetime.
 * An ID is unique within the app,
 * There is no guarantee it will be unique cross-applications
 */
public class IdCreatorUtil {

    /**
     * Randomly generates a UUID.
     *
     * @return a string representation of a randomly chosen 128-bit number
     */
    public static String getId() {
        return java.util.UUID.randomUUID().toString();
    }
}