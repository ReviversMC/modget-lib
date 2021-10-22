package com.github.nebelnidas.modget.modget_lib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModgetApi {
    public static final String NAMESPACE = "modget-lib";
    public static final String LOGGER_NAME = "Modget Library";


    private static Logger getLogger() {
        return LogManager.getLogger(LOGGER_NAME);
    }

    public static void logWarn(String name) {
        getLogger().warn(name);
    }
    public static void logWarn(String name, String msg) {
        getLogger().warn(String.format("%s: %s", name, msg));
    }

    public static void logInfo(String info) {
        getLogger().info(info);
    }
}
