package com.pakrt.pakrt.config;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class AccessInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Abaikan validasi untuk request OPTIONS (Pre-flight CORS)
        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        // Logika validasi hak akses berdasarkan HTTP Method dan path URL akan dieksekusi di sini
        // Sementara kita kembalikan true agar aplikasi tidak error saat di-build

        return true;
    }
}
