package com.github.nebelnidas.modgetlib;

import org.apache.logging.log4j.Logger.LogManager;
import org.apache.logging.log4j.Logger.Log;

public class ModgetLib {
    public static final String NAMESPACE = "modget-lib";
    public static final String LOGGER_NAME = "Modget-Lib";
    public static final MainManager MAIN_MANAGER = new MainManager();

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
