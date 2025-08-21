package com.uteq.turnos.catalog_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.interfaces.RSAPublicKey;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private final JwtKeyProvider keyProvider;
  private final String issuerRequired;
  private static final AntPathMatcher PM = new AntPathMatcher();

  // Endpoints públicos + preflight
  private static final String[] PUBLIC = new String[] {
      "/actuator/**",
      "/error" // muy importante, dejar pasar /error
  };

  public JwtAuthFilter(JwtKeyProvider keyProvider,
                       @org.springframework.beans.factory.annotation.Value("${security.jwt.issuer:auth-service}") String issuerRequired) {
    this.keyProvider = keyProvider;
    this.issuerRequired = issuerRequired;
  }

  @Override
  protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;
    final String uri = request.getRequestURI();
    for (String p : PUBLIC) if (PM.match(p, uri)) return true;
    return false;
  }

  @Override
  protected void doFilterInternal(@NonNull HttpServletRequest request,
                                  @NonNull HttpServletResponse response,
                                  @NonNull FilterChain chain) throws ServletException, IOException {
    try {
      String auth = request.getHeader("Authorization");
      if (auth != null && auth.startsWith("Bearer ")) {
        String token = auth.substring(7).trim();
        if (!token.isEmpty()) {
          RSAPublicKey pub = keyProvider.getPublicKey(); // trae y cachea la pública del auth-service
          Jws<Claims> jws = JwtUtils.parseAndVerify(token, pub, issuerRequired);
          Claims c = jws.getBody();

          // Si pones typ=access/refresh en el auth-service, evita aceptar refresh:
          Object typ = c.get("typ");
          if (typ != null && !"access".equals(String.valueOf(typ))) {
            // no autenticamos con refresh token
            chain.doFilter(request, response);
            return;
          }

          String subject = c.getSubject();               // suele ser userId
          String email   = c.get("email", String.class); // si lo pones en el token

          // roles: puede venir como CSV o como List
          Object rolesClaim = c.get("roles");
          List<SimpleGrantedAuthority> authorities;

          if (rolesClaim instanceof String s) {
            authorities = Arrays.stream(s.split(","))
                .map(String::trim).filter(r -> !r.isBlank())
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
          } else if (rolesClaim instanceof Collection<?> coll) {
            authorities = coll.stream()
                .flatMap(o -> o == null ? Stream.empty() : Stream.of(o.toString()))
                .map(String::trim).filter(r -> !r.isBlank())
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toList());
          } else {
            authorities = List.of();
          }

          var principal = (email != null && !email.isBlank()) ? email : subject;
          var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    } catch (Exception ignored) {
      // Token ausente/inválido → no seteamos auth; el EntryPoint responderá 401 si la ruta lo requiere
    }
    chain.doFilter(request, response);
  }
}
