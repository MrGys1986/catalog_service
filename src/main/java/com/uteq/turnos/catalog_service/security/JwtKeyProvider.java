package com.uteq.turnos.catalog_service.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;

@Component
public class JwtKeyProvider {

  @Value("${security.jwt.public-key-url:http://localhost:8081/auth/keys/public}")
  private String publicKeyUrl;

  private final RestTemplate http = new RestTemplate();

  private volatile RSAPublicKey cached;
  private volatile Instant nextReload = Instant.MIN;

  public RSAPublicKey getPublicKey() {
    if (cached == null || Instant.now().isAfter(nextReload)) {
      synchronized (this) {
        if (cached == null || Instant.now().isAfter(nextReload)) {
          cached = fetch();
          nextReload = Instant.now().plus(Duration.ofMinutes(10)); // cache 10 min
        }
      }
    }
    return cached;
  }

  private RSAPublicKey fetch() {
    String pem = http.getForObject(publicKeyUrl, String.class);
    if (pem == null) throw new IllegalStateException("No se pudo obtener clave pública");
    String base64 = pem.replace("-----BEGIN PUBLIC KEY-----", "")
                       .replace("-----END PUBLIC KEY-----", "")
                       .replaceAll("\\s", "");
    try {
      byte[] der = Base64.getDecoder().decode(base64);
      var spec = new X509EncodedKeySpec(der);
      var kf = KeyFactory.getInstance("RSA");
      return (RSAPublicKey) kf.generatePublic(spec);
    } catch (Exception e) {
      throw new IllegalStateException("Clave pública inválida", e);
    }
  }
}
