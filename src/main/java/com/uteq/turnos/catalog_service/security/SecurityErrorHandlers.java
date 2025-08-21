package com.uteq.turnos.catalog_service.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SecurityErrorHandlers {

  @Bean
  public org.springframework.security.web.AuthenticationEntryPoint authenticationEntryPoint() {
    return (request, response, ex) -> writeJson(response, request, 401,
      "UNAUTHORIZED", "Se requiere autenticación (token ausente o inválido)");
  }

  @Bean
  public org.springframework.security.web.access.AccessDeniedHandler accessDeniedHandler() {
    return (request, response, ex) -> writeJson(response, request, 403,
      "FORBIDDEN", "No tienes permisos para acceder a este recurso");
  }

  private void writeJson(HttpServletResponse res, HttpServletRequest req, int code, String error, String msg) throws IOException {
    res.setStatus(code);
    res.setContentType("application/json");
    res.setHeader("X-Auth-Error", error);
    res.getWriter().write("""
      {"status":%d,"error":"%s","message":"%s","path":"%s","method":"%s"}
    """.formatted(code, error, msg, req.getRequestURI(), req.getMethod()));
  }
}
