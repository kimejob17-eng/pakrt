package com.pakrt.pakrt.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Logging {

    private static final Logger logger = LoggerFactory.getLogger(Logging.class);

    public static void LogInfo(String message) {
        logger.info("[USER: {}] {}", General.getUsername(), message);
    }

    public static void LogError(String message) {
        logger.error("[USER: {}] [ERROR] {}", General.getUsername(), message);
    }

    public static void LogWarn(String message) {
        logger.warn("[USER: {}] [WARN] {}", General.getUsername(), message);
    }

    public static void LogDebug(String message) {
        logger.debug("[USER: {}] [DEBUG] {}", General.getUsername(), message);
    }
}
