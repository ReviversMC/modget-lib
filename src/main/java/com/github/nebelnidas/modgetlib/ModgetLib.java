package com.github.nebelnidas.modgetlib;

import java.util.ArrayList;

import com.github.nebelnidas.modgetlib.data.RecognizedMod;
import com.github.nebelnidas.modgetlib.manager.MainManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModgetLib {
    public static final String NAMESPACE = "modget-lib";
    public static final String LOGGER_NAME = "Modget-Lib";
    public static MainManager MAIN_MANAGER;

    public ModgetLib(String minecraftVersion, ArrayList<RecognizedMod> installedMods) {
        MAIN_MANAGER = new MainManager(minecraftVersion, installedMods);
    }

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
