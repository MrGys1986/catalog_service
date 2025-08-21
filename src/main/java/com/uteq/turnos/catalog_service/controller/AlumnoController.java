package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.dto.AlumnoMateriaDTO;
import com.uteq.turnos.catalog_service.model.Alumno;
import com.uteq.turnos.catalog_service.repo.AlumnoRepo;
import com.uteq.turnos.catalog_service.service.AsignacionesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/alumnos")
public class AlumnoController {

  private static final Logger log = LoggerFactory.getLogger(AlumnoController.class);

  private final AlumnoRepo alumnos;
  private final AsignacionesService asig;
  private final RestTemplate http;

  @Value("${app.auth.base-url:http://localhost:8081}")
  private String authBase;

  public AlumnoController(AlumnoRepo alumnos, AsignacionesService asig, RestTemplate http) {
    this.alumnos = alumnos;
    this.asig = asig;
    this.http = http;
  }

  // ===== CRUD básico con repo (evita conflicto de firmas del service) =====
  @GetMapping
  public List<Alumno> list() { return alumnos.findAll(); }

  @GetMapping("/{id}")
  public Alumno get(@PathVariable Long id) {
    return alumnos.findById(id).orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Alumno create(@RequestBody Alumno a) {
    if (a.getUserId() == null) throw new IllegalArgumentException("userId es requerido");
    if (a.getActivo() == null) a.setActivo(Boolean.TRUE);
    return alumnos.save(a);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Alumno update(@PathVariable Long id, @RequestBody Alumno body) {
    Alumno a = alumnos.findById(id).orElseThrow(() -> new IllegalArgumentException("Alumno no encontrado"));
    if (body.getNoControl() != null) a.setNoControl(body.getNoControl());
    if (body.getActivo() != null) a.setActivo(body.getActivo());
    // userId no se toca aquí por seguridad (lo gestiona auth-service)
    return alumnos.save(a);
  }

  /**
   * Elimina del catálogo y, si se provee userId (o se puede obtener antes de borrar),
   * intenta también borrar en auth-service: DELETE {authBase}/admin/users/{userId}.
   */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id, @RequestParam(required = false) Long userId) {
    Long uid = userId != null
        ? userId
        : alumnos.findById(id).map(Alumno::getUserId).orElse(null);

    alumnos.deleteById(id);

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

  // ====== RUTAS DE MATERIAS (las que usa tu vista AsignarMaterias.jsx) ======

  /** GET /alumnos/{alumnoId}/materias -> lista de inscripciones (DTO liviano) */
  @GetMapping("/{alumnoId}/materias")
  @PreAuthorize("hasAnyRole('ADMIN','DOCENTE', 'ALUMNO')")
  public List<AlumnoMateriaDTO> materiasDeAlumno(@PathVariable Long alumnoId) {
    return asig.materiasDeAlumno(alumnoId).stream().map(AlumnoMateriaDTO::from).toList();
  }

  /** POST /alumnos/{alumnoId}/inscribir/{materiaId} */
  @PostMapping("/{alumnoId}/inscribir/{materiaId}")
  @PreAuthorize("hasRole('ADMIN')")
  public AlumnoMateriaDTO inscribir(@PathVariable Long alumnoId, @PathVariable Long materiaId) {
    return AlumnoMateriaDTO.from(asig.inscribir(alumnoId, materiaId));
  }

  /** DELETE /alumnos/{alumnoId}/desinscribir/{materiaId} */
  @DeleteMapping("/{alumnoId}/desinscribir/{materiaId}")
  @PreAuthorize("hasRole('ADMIN')")
  public void desinscribir(@PathVariable Long alumnoId, @PathVariable Long materiaId) {
    asig.desinscribir(alumnoId, materiaId);
  }
}
