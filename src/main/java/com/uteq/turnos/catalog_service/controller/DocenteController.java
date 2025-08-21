package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.Docente;
import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.repo.DocenteRepo;
import com.uteq.turnos.catalog_service.repo.DivisionRepo;
import com.uteq.turnos.catalog_service.service.DocenteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/docentes")
public class DocenteController {

  private final DocenteRepo docenteRepo;
  private final DivisionRepo divisionRepo;
  private final RestTemplate http;
  private final DocenteService svc;

  @Value("${app.auth.base-url:http://localhost:8081}")
  private String authBase;

  public DocenteController(DocenteRepo docenteRepo,
                           DivisionRepo divisionRepo,
                           RestTemplate http,
                           DocenteService svc) {
    this.docenteRepo = docenteRepo;
    this.divisionRepo = divisionRepo;
    this.http = http;
    this.svc = svc;
  }

  /** Listado completo: sólo ADMIN */
  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  public List<Docente> list() {
    return docenteRepo.findAll();
  }

  /** Filtro por división como query param: accesible a ADMIN/DOCENTE/ALUMNO */
  @GetMapping(params = "divisionId")
  @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','ALUMNO')")
  public List<Docente> listByDivisionParam(@RequestParam Long divisionId) {
    return svc.listByDivision(divisionId);
  }

  /** Alternativa con path param: accesible a ADMIN/DOCENTE/ALUMNO */
  @GetMapping("/by-division/{divisionId}")
  @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','ALUMNO')")
  public List<Docente> listByDivisionPath(@PathVariable Long divisionId) {
    return svc.listByDivision(divisionId);
  }

  /** (Opcional) Obtener un docente por ID: autenticado */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public Docente get(@PathVariable Long id) {
    return docenteRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
  }

  /** Crear: sólo ADMIN */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Docente create(@RequestBody Map<String, Object> body) {
    Long userId = body.get("userId") == null ? null : Long.valueOf(String.valueOf(body.get("userId")));
    if (userId == null) throw new IllegalArgumentException("userId es requerido");

    Long divisionId = body.get("divisionId") == null ? null : Long.valueOf(String.valueOf(body.get("divisionId")));
    if (divisionId == null) throw new IllegalArgumentException("divisionId es requerido");

    Division div = divisionRepo.findById(divisionId)
        .orElseThrow(() -> new IllegalArgumentException("División no encontrada"));

    Docente d = Docente.builder()
        .userId(userId)
        .noEmpleado(Objects.toString(body.getOrDefault("noEmpleado", "")))
        .division(div)
        .activo(Boolean.valueOf(String.valueOf(body.getOrDefault("activo", true))))
        .build();

    return docenteRepo.save(d);
  }

  /** Actualizar: sólo ADMIN */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Docente update(@PathVariable Long id, @RequestBody Map<String, Object> patch) {
    Docente d = docenteRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));

    if (patch.containsKey("noEmpleado")) {
      d.setNoEmpleado(Objects.toString(patch.get("noEmpleado"), ""));
    }
    if (patch.containsKey("activo")) {
      d.setActivo(Boolean.valueOf(String.valueOf(patch.get("activo"))));
    }
    if (patch.containsKey("divisionId")) {
      Long divisionId = Long.valueOf(String.valueOf(patch.get("divisionId")));
      Division div = divisionRepo.findById(divisionId)
          .orElseThrow(() -> new IllegalArgumentException("División no encontrada"));
      d.setDivision(div);
    }

    return docenteRepo.save(d);
  }

  /**
   * Eliminar docente del catálogo y (mejor esfuerzo) su usuario en auth-service.
   * Para borrar en auth se usa el mismo JWT del caller (Authorization header).
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id,
                                     @RequestHeader(value = "Authorization", required = false) String authz) {
    Docente d = docenteRepo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Docente no encontrado"));
    Long userId = d.getUserId();

    // 1) Borra en catálogo
    docenteRepo.deleteById(id);

    // 2) (best effort) Borra su usuario en auth-service
    if (userId != null && authz != null && !authz.isBlank()) {
      try {
        HttpHeaders h = new HttpHeaders();
        h.set("Authorization", authz);
        HttpEntity<Void> req = new HttpEntity<>(null, h);
        http.exchange(authBase + "/admin/users/" + userId, HttpMethod.DELETE, req, Void.class);
      } catch (HttpClientErrorException.NotFound ignored) {
        // usuario ya no existe en auth, no pasa nada
      } catch (Exception ignored) {
        // no romper si auth está caído
      }
    }

    return ResponseEntity.noContent().build();
  }
}
