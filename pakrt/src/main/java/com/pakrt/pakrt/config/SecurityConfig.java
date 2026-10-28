package com.pakrt.pakrt.config;

import com.pakrt.pakrt.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Mematikan CSRF karena otentikasi menggunakan token JWT yang dikirim via Header
                .csrf(csrf -> csrf.disable())

                // Mengaktifkan filter CORS dengan aturan dari corsConfigurationSource()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Memastikan aplikasi berjalan STATELESS (tanpa sesi memori)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Mengatur izin akses endpoint
                .authorizeHttpRequests(auth -> auth
                        // Endpoint publik (Login & Registrasi Akun)
                        .requestMatchers(HttpMethod.POST, "/auth/token", "/api/v1/auth/**", "/api/v1/account/create").permitAll()
                        // Endpoint publik Menu & Inisialisasi
                        .requestMatchers("/api/v1/menu/**", "/api/v1/menu-detail/**").permitAll()
                        // SEMUA endpoint data (/api/v1/account/list, /api/v1/warga/**, dll) WAJIB Bearer Token
                        .anyRequest().authenticated()
                )
                // Format response error 401 Unauthorized JSON standar Nexus
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setContentType("application/json;charset=UTF-8");
                            response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"responseCode\":\"401001\",\"responseDesc\":\"Unauthorized: Token Bearer tidak ditemukan atau sudah kedaluwarsa (> 10 menit)\",\"data\":null}");
                        })
                )
                // Filter verifikasi JWT Bearer Token (10 Menit Validasi)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Mendaftarkan domain/port yang diizinkan mengakses API (Frontend Web & Mobile)
        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",     // Port Vite React saat port 5173 sedang digunakan
                "http://localhost:8081",     // Origin standar aplikasi Capacitor Android
                "https://app.pakrt.my.id"    // Domain production PWA nantinya
        ));

        // Membatasi metode HTTP yang diperbolehkan
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Mengizinkan Header khusus untuk membawa Bearer Token
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}

