package com.pakrt.pakrt.responses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Standard ResponseBuilder helper sesuai konvensi Nexus Multilateral Backend.
 */
public class ResponseBuilder {

    /**
     * Membangun HTTP 200 OK dengan format standar ResultResponse:
     * {
     *   "responseCode": "200100",
     *   "responseData": [
     *     { "result": { ... } }
     *   ]
     * }
     */
    public static ResponseEntity<?> buildSuccessResponse(Object data) {
        Map<String, Object> wrapped = new HashMap<>();
        wrapped.put("result", data);
        ResultResponse response = ResultResponse.Get("200100", Collections.singletonList(wrapped));
        return ResponseEntity.ok(response);
    }

    /**
     * Membangun HTTP 400 Bad Request dengan format standar BasicResponse:
     * {
     *   "responseCode": "400xxx",
     *   "responseMessage": "..."
     * }
     */
    public static ResponseEntity<?> buildErrorResponse(String responseCode, String responseMessage) {
        return ResponseEntity.badRequest().body(BasicResponse.Get(responseCode, responseMessage));
    }

    /**
     * Membangun HTTP response otorisasi (misal HTTP 401 Unauthorized / HTTP 403 Forbidden):
     * {
     *   "responseCode": "401001",
     *   "responseMessage": "..."
     * }
     */
    public static ResponseEntity<?> authResponse(String responseCode, String responseMessage, HttpStatus status) {
        return ResponseEntity.status(status).body(BasicResponse.Get(responseCode, responseMessage));
    }
}
