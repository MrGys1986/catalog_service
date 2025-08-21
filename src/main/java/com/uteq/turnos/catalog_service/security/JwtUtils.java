package com.uteq.turnos.catalog_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

import java.security.Key;

final class JwtUtils {
  private JwtUtils(){}

  static Jws<Claims> parseAndVerify(String token, Key verifyKey, String requiredIssuer) {
    return Jwts
      .parserBuilder()
      .setSigningKey(verifyKey) // RS256: usa la pública para verificar
      .requireIssuer(requiredIssuer) // debe coincidir con lo que firma tu auth-service
      .build()
      .parseClaimsJws(token);
  }
}

