package com.seguros.polizas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@Slf4j
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Value("${api.security.key}")
    private String expectedApiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getRequestURI().contains("swagger") || request.getRequestURI().contains("api-docs")) {
            return true;
        }

        String apiKey = request.getHeader("api-key");
        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            log.warn("Intento de acceso no autorizado a la ruta: {}", request.getRequestURI());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Acceso denegado: api-key invalido o ausente\"}");
            response.setContentType("application/json");
            return false;
        }
        return true;
    }
}