package com.pakrt.pakrt.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataMap {

    /**
     * Membungkus list data ke format standar Nexus:
     * [ { "result": [...], "total": N } ]
     */
    public static List<Map<String, Object>> Mapped(List<?> result) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result != null ? result : Collections.emptyList());
        map.put("total", result != null ? result.size() : 0);
        return Collections.singletonList(map);
    }

    /**
     * Membungkus list data berhalaman (pagination) ke format standar Nexus:
     * [ { "result": [...], "total": totalRecords } ]
     */
    public static List<Map<String, Object>> Mapped(List<?> result, long total) {
        Map<String, Object> map = new HashMap<>();
        map.put("result", result != null ? result : Collections.emptyList());
        map.put("total", total);
        return Collections.singletonList(map);
    }

    public static Map<String, Object> generate(List<?> result, long total) {
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("total", total);
        return response;
    }
}