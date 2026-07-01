package com.example.multitenant.security;

import com.example.multitenant.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message: {}", authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final ApiResponse<Object> body = ApiResponse.error("Unauthorized: " + authException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

