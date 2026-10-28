package com.pakrt.pakrt.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Standard basic response envelope sesuai standar Nexus Multilateral Backend.
 * Format: { "responseCode": "...", "responseMessage": "..." }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicResponse implements Serializable {

    private String responseCode;
    private String responseMessage;

    public static BasicResponse Get(String responseCode, String responseMessage) {
        return new BasicResponse(responseCode, responseMessage);
    }
}
