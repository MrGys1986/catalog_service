package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.Administrador;
import com.uteq.turnos.catalog_service.repo.AdministradorRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {

  private static final Logger log = LoggerFactory.getLogger(AdministradorController.class);

  private final AdministradorRepo adminRepo;
  private final RestTemplate http;

  @Value("${app.auth.base-url:http://localhost:8081}")
  private String authBase;

  public AdministradorController(AdministradorRepo adminRepo, RestTemplate http) {
    this.adminRepo = adminRepo;
    this.http = http;
  }

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<Administrador> list() {
    return adminRepo.findAll();
  }

  /** Crea sólo el registro en catálogo (espera que ya exista el userId en auth). */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Administrador create(@RequestBody Map<String, Object> body) {
    Long userId = body.get("userId") == null ? null : Long.valueOf(String.valueOf(body.get("userId")));
    if (userId == null) throw new IllegalArgumentException("userId es requerido");

    Administrador a = Administrador.builder()
        .userId(userId)
        .noTrabajador(String.valueOf(body.getOrDefault("noTrabajador", "")))
        .activo(Boolean.valueOf(String.valueOf(body.getOrDefault("activo", true))))
        .build();
    return adminRepo.save(a);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Administrador update(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
    Administrador a = adminRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Administrador no encontrado"));

    if (patch.containsKey("noTrabajador")) {
      a.setNoTrabajador(Objects.toString(patch.get("noTrabajador"), ""));
    }
    if (patch.containsKey("activo")) {
      a.setActivo(Boolean.valueOf(String.valueOf(patch.get("activo"))));
    }
    return adminRepo.save(a);
  }

  /**
   * Elimina del catálogo y, si se provee userId (o se puede obtener antes de borrar),
   * intenta también borrar en auth-service: DELETE {authBase}/admin/users/{userId}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id,
                     @RequestParam(required = false) Long userId) {

    // Obtener userId si no vino como query param (leer ANTES de borrar)
    Long uid = userId != null
        ? userId
        : adminRepo.findById(id).map(Administrador::getUserId).orElse(null);

    // 1) Borrar en catálogo
    adminRepo.deleteById(id);

    // 2) Best-effort: borrar también en auth-service
    if (uid != null) {
      try {
        http.delete(authBase + "/admin/users/{id}", uid);
      } catch (HttpClientErrorException.NotFound nf) {
        log.info("Usuario en auth-service ya no existía (userId={})", uid);
      } catch (Exception ex) {
        log.warn("No se pudo borrar el usuario en auth-service (userId={}): {}", uid, ex.getMessage());
      }
    }
  }
}
