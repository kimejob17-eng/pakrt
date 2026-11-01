package com.pakrt.pakrt.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pakrt.pakrt.models.account.Account;
import com.pakrt.pakrt.repositories.account.AccountRepository;
import com.pakrt.pakrt.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.*;

/**
 * AccessInterceptor - Dynamic RBAC Enforcement Engine (Nexus Multilateral Architecture).
 * Memvalidasi otorisasi dan hak akses dinamis (RBAC) setiap HTTP Request.
 * Mengembalikan HTTP 401 Unauthorized jika token tidak ada, kedaluwarsa, atau izin akses tidak dimiliki.
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AccountRepository accountRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. Abaikan validasi untuk request OPTIONS (Pre-flight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod().toUpperCase();

        // 2. Endpoint publik yang tidak memerlukan token / otorisasi
        if (isPublicEndpoint(uri, method, request)) {
            return true;
        }

        // 3. Ekstrak Header Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(response, "401001", "Unauthorized: Token Bearer tidak ditemukan atau tidak valid");
            return false;
        }

        String token = authHeader.substring(7).trim();
        if (!jwtService.validateToken(token)) {
            sendUnauthorized(response, "401001", "Unauthorized: Sesi kedaluwarsa atau token tidak valid (> 10 menit)");
            return false;
        }

        String username = jwtService.extractUsername(token);
        if (username == null || username.trim().isEmpty()) {
            sendUnauthorized(response, "401001", "Unauthorized: Username tidak valid dalam token");
            return false;
        }

        // 4. Cari akun di database
        Optional<Account> accountOpt = accountRepository.findByUsername(username);
        if (accountOpt.isEmpty()) {
            sendUnauthorized(response, "401002", "Unauthorized: Akun pengguna tidak ditemukan di database");
            return false;
        }

        Account account = accountOpt.get();
        if (Boolean.FALSE.equals(account.getIsActive())) {
            sendUnauthorized(response, "401002", "Unauthorized: Akun Anda telah dinonaktifkan. Silakan hubungi pengurus RT");
            return false;
        }

        // 5. Ekstrak permissions dinamis (Prioritas: Akun -> Fallback ke Role)
        String rawPerms = account.getPermissions();
        if ((rawPerms == null || rawPerms.trim().isEmpty()) && account.getRole() != null) {
            rawPerms = account.getRole().getPermissions();
        }

        Set<String> permissions = parsePermissions(rawPerms);

        // 6. Evaluasi Hak Akses Dinamis (RBAC):
        // Role SUPERADMIN atau izin wildcard '*' memiliki hak akses menyeluruh
        boolean isSuperAdminRole = account.getRole() != null &&
                ("SUPERADMIN".equalsIgnoreCase(account.getRole().getNamaRole()) ||
                 "SUPER_ADMIN".equalsIgnoreCase(account.getRole().getNamaRole()) ||
                 "ADMIN".equalsIgnoreCase(account.getRole().getNamaRole()));

        if (isSuperAdminRole || permissions.contains("*")) {
            return true;
        }

        if (isAccessAllowed(uri, method, permissions)) {
            return true;
        }

        // 7. Jika izin tidak ditemukan, kirim HTTP 401 Unauthorized
        sendUnauthorized(response, "401003", "Unauthorized: Anda tidak memiliki hak akses untuk API ini (" + method + " " + uri + ")");
        return false;
    }

    /**
     * Memeriksa apakah endpoint bersifat publik
     */
    private boolean isPublicEndpoint(String uri, String method, HttpServletRequest request) {
        if (uri.startsWith("/auth/token") || uri.startsWith("/api/v1/auth/")) {
            return true;
        }
        if (uri.equals("/error")) {
            return true;
        }
        return false;
    }

    /**
     * Parsing JSON / String permissions ke Set<String>
     */
    private Set<String> parsePermissions(String rawPerms) {
        Set<String> set = new HashSet<>();
        if (rawPerms == null || rawPerms.trim().isEmpty()) {
            return set;
        }

        String trimmed = rawPerms.trim();
        if (trimmed.startsWith("[")) {
            try {
                List<String> list = objectMapper.readValue(trimmed, new TypeReference<List<String>>() {});
                if (list != null) {
                    for (String item : list) {
                        if (item != null && !item.trim().isEmpty()) {
                            set.add(item.trim());
                        }
                    }
                }
                return set;
            } catch (Exception ignored) {
                String clean = trimmed.replaceAll("[\\[\\]\"\\s]", "");
                for (String p : clean.split(",")) {
                    if (!p.trim().isEmpty()) set.add(p.trim());
                }
                return set;
            }
        }

        for (String p : trimmed.split(",")) {
            if (!p.trim().isEmpty()) set.add(p.trim());
        }
        return set;
    }

    /**
     * Mengecek kecocokan hak akses berdasarkan URI dan HTTP Method
     */
    private boolean isAccessAllowed(String uri, String method, Set<String> permissions) {
        // Normalisasi URI dengan placeholder {id} untuk path variabel UUID atau ID angka
        String normalizedUri = uri.replaceAll("/[0-9a-fA-F\\-]{36}", "/{id}")
                                  .replaceAll("/\\d+", "/{id}");

        // 1. Cek kecocokan path langsung (misal: "/api/v1/account/list", "/api/v1/menu/tree", dll)
        if (permissions.contains(uri) || permissions.contains(normalizedUri)) {
            return true;
        }

        // 2. Menu Tree endpoint untuk navigasi frontend
        if (uri.equals("/api/v1/menu/tree") && "GET".equalsIgnoreCase(method)) {
            return true;
        }

        // 3. Mapping Modul Berdasarkan Prefix URI
        String module = null;
        String uiPath = null;
        List<String> alternateModules = new ArrayList<>();

        if (uri.startsWith("/api/v1/account")) {
            if (uri.contains("/roles")) {
                module = "role-access";
                uiPath = "/app/role-access";
                alternateModules.add("account");
            } else {
                module = "account";
                uiPath = "/app/account";
                alternateModules.add("role-access");
            }
        } else if (uri.startsWith("/api/v1/warga")) {
            module = "warga";
            uiPath = "/app/warga";
            alternateModules.add("data-warga");
            alternateModules.add("kartu-keluarga");
        } else if (uri.startsWith("/api/v1/kartu-keluarga")) {
            module = "kartu-keluarga";
            uiPath = "/app/kartu-keluarga";
            alternateModules.add("warga");
            alternateModules.add("data-warga");
        } else if (uri.startsWith("/api/v1/status-keluarga")) {
            module = "status-keluarga";
            alternateModules.add("kartu-keluarga");
            alternateModules.add("warga");
            alternateModules.add("data-warga");
        } else if (uri.startsWith("/api/v1/menu-detail")) {
            module = "menu-detail";
            uiPath = "/app/menu-detail";
            alternateModules.add("menu");
            alternateModules.add("kelola-menu");
        } else if (uri.startsWith("/api/v1/menu")) {
            module = "menu";
            uiPath = "/app/menu";
            alternateModules.add("kelola-menu");
        } else if (uri.startsWith("/api/v1/logs")) {
            module = "logs";
            uiPath = "/app/logs";
        }

        if (module == null) {
            return false;
        }

        // Cek izin berbasis UI Path
        if (uiPath != null && permissions.contains(uiPath)) {
            return true;
        }

        // Cek izin berbasis Module Key penuh
        if (permissions.contains(module)) {
            return true;
        }
        for (String alt : alternateModules) {
            if (permissions.contains(alt)) {
                return true;
            }
        }

        // Cek izin berbasis C / R / U / D
        if ("GET".equalsIgnoreCase(method)) {
            if (permissions.contains(module + ":Read") ||
                permissions.contains(module + ":List") ||
                permissions.contains(module + ":View")) {
                return true;
            }
            for (String alt : alternateModules) {
                if (permissions.contains(alt + ":Read") ||
                    permissions.contains(alt + ":List") ||
                    permissions.contains(alt + ":View")) {
                    return true;
                }
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            if (uri.endsWith("/delete")) {
                if (permissions.contains(module + ":Delete") ||
                    permissions.contains(module + ":Delete (POST)")) {
                    return true;
                }
                for (String alt : alternateModules) {
                    if (permissions.contains(alt + ":Delete") ||
                        permissions.contains(alt + ":Delete (POST)")) {
                        return true;
                    }
                }
                return false;
            }
            if (permissions.contains(module + ":Create")) {
                return true;
            }
            for (String alt : alternateModules) {
                if (permissions.contains(alt + ":Create")) {
                    return true;
                }
            }
            return false;
        } else if ("PUT".equalsIgnoreCase(method)) {
            if (permissions.contains(module + ":Update")) {
                return true;
            }
            for (String alt : alternateModules) {
                if (permissions.contains(alt + ":Update")) {
                    return true;
                }
            }
            return false;
        } else if ("DELETE".equalsIgnoreCase(method)) {
            if (permissions.contains(module + ":Delete")) {
                return true;
            }
            for (String alt : alternateModules) {
                if (permissions.contains(alt + ":Delete")) {
                    return true;
                }
            }
            return false;
        }

        return false;
    }

    /**
     * Mengirimkan HTTP 401 Unauthorized Response dalam format JSON standar Nexus
     */
    private void sendUnauthorized(HttpServletResponse response, String code, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String json = "{\"responseCode\":\"" + code + "\",\"responseDesc\":\"" + escapeJson(message) + "\",\"data\":null}";
        response.getWriter().write(json);
    }

    private String escapeJson(String raw) {
        if (raw == null) return "";
        return raw.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
