package com.pakrt.pakrt.utils;

import java.util.HashMap;
import java.util.Map;

public class MessageDictionary {

    private static final Map<String, String> DICTIONARY = new HashMap<>();

    static {
        DICTIONARY.put("200100", "Success");
        DICTIONARY.put("201100", "Created");
        DICTIONARY.put("400200", "Username / Password Invalid");
        DICTIONARY.put("400201", "Invalid Field Format");
        DICTIONARY.put("400202", "Missing Mandatory Field");
        DICTIONARY.put("400203", "User Is Exist");
        DICTIONARY.put("400204", "Data Is Exist");
        DICTIONARY.put("400404", "Data Is Not Found");
        DICTIONARY.put("400409", "Data Is Already Same");
        DICTIONARY.put("400500", "Internal Server Error");
        DICTIONARY.put("401001", "Unauthorized");
    }

    public static String MessageEn(String code, String param) {
        String baseMessage = DICTIONARY.getOrDefault(code, "Unknown Error");
        if (param != null && !param.trim().isEmpty()) {
            if ("400204".equals(code)) {
                return "Data Is Exist [ " + param + " ]";
            }
            if ("400404".equals(code)) {
                return "Data Is Not Found [ " + param + " ]";
            }
            if ("400202".equals(code)) {
                return "Missing Mandatory Field [ " + param + " ]";
            }
            return baseMessage + " [ " + param + " ]";
        }
        return baseMessage;
    }

    public static String getMessage(String code) {
        return DICTIONARY.getOrDefault(code, "Deskripsi pesan tidak terdefinisi");
    }
}