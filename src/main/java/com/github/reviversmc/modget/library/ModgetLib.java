package com.github.reviversmc.modget.library;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import lombok.NonNull;

public class ModgetLib {
    public static final String NAMESPACE = "modget-lib";
    public static final String LOGGER_NAME = "Modget Library";


    private static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    public static void logWarn(@NonNull String message) {
        getLogger().warn(message);
    }
    public static void logWarn(@NonNull String info, String message) {
        getLogger().warn(String.format("%s: %s", info, message));
    }

    public static void logInfo(@NonNull String message) {
        getLogger().info(message);
    }
}
