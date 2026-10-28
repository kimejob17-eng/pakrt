package com.pakrt.pakrt.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Standard result response envelope sesuai standar Nexus Multilateral Backend.
 * Format: { "responseCode": "...", "responseData": [ ... ] }
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultResponse implements Serializable {

    private String responseCode;
    private List<?> responseData;

    public static ResultResponse Get(String responseCode, List<?> responseData) {
        return new ResultResponse(responseCode, responseData);
    }

    public static ResultResponse Get(String responseCode, Object responseData) {
        if (responseData instanceof List<?>) {
            return new ResultResponse(responseCode, (List<?>) responseData);
        }
        return new ResultResponse(responseCode, Collections.singletonList(responseData));
    }
}
