package com.uteq.turnos.catalog_service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http,
                                         JwtAuthFilter jwtFilter,
                                         AuthenticationEntryPoint entryPoint,
                                         AccessDeniedHandler deniedHandler) throws Exception {
    http
      .cors(c -> c.configurationSource(corsConfig()))
      .csrf(cs -> cs.disable())
      .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(eh -> eh
          .authenticationEntryPoint(entryPoint)
          .accessDeniedHandler(deniedHandler)
      )
      .authorizeHttpRequests(reg -> reg
          .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
          .requestMatchers("/actuator/**", "/error").permitAll()
          // 👇 abre lo que deba ser público; deja el resto autenticado:
          // .requestMatchers(HttpMethod.GET, "/alumnos/**").hasAnyRole("ADMIN","DOCENTE","ALUMNO")
          .anyRequest().authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public CorsConfigurationSource corsConfig() {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of("http://localhost:5173","http://localhost:3000"));
    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("Authorization","Content-Type"));
    c.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", c);
    return src;
  }
}
