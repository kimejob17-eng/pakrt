package com.pakrt.pakrt.controllers;

import com.pakrt.pakrt.responses.ResponseBuilder;
import com.pakrt.pakrt.responses.ResultResponse;
import com.pakrt.pakrt.services.LogService;
import com.pakrt.pakrt.utils.Logging;
import com.pakrt.pakrt.utils.MessageDictionary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/logs")
public class LogController {

    @Autowired
    private LogService logService;

    /**
     * Endpoint untuk melihat riwayat aktivitas user (Audit Log).
     * GET /api/v1/logs/list?username=...&action=...&pageNo=0&pageSize=10
     */
    @GetMapping("/list")
    public ResponseEntity<?> getLogs(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String action,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        try {
            Logging.LogInfo(String.format("Get Log List: username=%s, action=%s, pageNo=%d, pageSize=%d",
                    username, action, pageNo, pageSize));

            List<Map<String, Object>> logs = logService.getLogs(username, action, pageNo, pageSize);
            return ResponseEntity.ok(ResultResponse.Get("200100", logs));
        } catch (Exception e) {
            Logging.LogError("Error fetching logs: " + e.getMessage());
            return ResponseBuilder.buildErrorResponse("400500", MessageDictionary.MessageEn("400500", e.getMessage()));
        }
    }
}
