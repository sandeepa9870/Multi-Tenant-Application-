package com.example.multitenant.security;

import com.example.multitenant.util.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Responding with access denied error. Message: {}", accessDeniedException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final ApiResponse<Object> body = ApiResponse.error("Access Denied: " + accessDeniedException.getMessage());
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}

