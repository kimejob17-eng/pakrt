package com.pakrt.pakrt.services;

import com.pakrt.pakrt.models.log.UserLog;
import com.pakrt.pakrt.repositories.log.UserLogRepository;
import com.pakrt.pakrt.utils.DataMap;
import com.pakrt.pakrt.utils.General;
import com.pakrt.pakrt.utils.Logging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LogService {

    @Autowired
    private UserLogRepository userLogRepository;

    /**
     * Mencatat log aktivitas user standar (seperti pola Nexus: logService.log("Create", entity, "/endpoint")).
     */
    public void log(String action, Object data, String endpoint) {
        log(action, data, endpoint, General.getClientIp(), "SUCCESS");
    }

    /**
     * Mencatat log aktivitas user dengan custom IP address.
     */
    public void log(String action, Object data, String endpoint, String ipAddress) {
        log(action, data, endpoint, ipAddress, "SUCCESS");
    }

    /**
     * Mencatat log aktivitas user lengkap dengan status (SUCCESS / FAILED).
     */
    public void log(String action, Object data, String endpoint, String ipAddress, String status) {
        try {
            UserLog userLog = new UserLog();
            userLog.setUsername(General.getUsername());
            userLog.setAction(action);
            userLog.setEndpoint(endpoint);
            userLog.setPayload(General.stringToJson(data));
            userLog.setIpAddress(ipAddress != null ? ipAddress : General.getClientIp());
            userLog.setStatus(status != null ? status : "SUCCESS");

            userLogRepository.save(userLog);
        } catch (Exception e) {
            Logging.LogError("Gagal menyimpan UserLog: " + e.getMessage());
        }
    }

    /**
     * Mengambil daftar log aktivitas user dengan pagination & filter format Nexus.
     */
    public List<Map<String, Object>> getLogs(String username, String action, Integer pageNo, Integer pageSize) {
        int page = (pageNo != null && pageNo >= 0) ? pageNo : 0;
        int size = (pageSize != null && pageSize > 0) ? pageSize : 10;

        Pageable pageable = PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());

        org.springframework.data.jpa.domain.Specification<UserLog> spec = (root, query, cb) -> {
            java.util.List<jakarta.persistence.criteria.Predicate> predicates = new java.util.ArrayList<>();
            if (username != null && !username.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + username.trim().toLowerCase() + "%"));
            }
            if (action != null && !action.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("action"), action.trim()));
            }
            return cb.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<UserLog> logPage = userLogRepository.findAll(spec, pageable);

        return DataMap.Mapped(logPage.getContent(), logPage.getTotalElements());
    }
}
