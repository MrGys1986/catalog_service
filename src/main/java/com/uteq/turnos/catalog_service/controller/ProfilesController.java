// src/main/java/com/uteq/turnos/catalog_service/controller/ProfilesController.java
package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.Administrador;
import com.uteq.turnos.catalog_service.model.Alumno;
import com.uteq.turnos.catalog_service.model.Docente;
import com.uteq.turnos.catalog_service.repo.AdministradorRepo;
import com.uteq.turnos.catalog_service.repo.AlumnoRepo;
import com.uteq.turnos.catalog_service.repo.DocenteRepo;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {

  private final AlumnoRepo alumnos;
  private final DocenteRepo docentes;
  private final AdministradorRepo admins;

  public ProfilesController(AlumnoRepo alumnos, DocenteRepo docentes, AdministradorRepo admins) {
    this.alumnos = alumnos;
    this.docentes = docentes;
    this.admins = admins;
  }

  /**
   * Devuelve { alumnoId, docenteId, adminId } para el userId indicado.
   * - Si no mandas userId, intenta tomarlo del Authentication.getName() (sub del JWT).
   * - Requiere estar autenticado, pero no exige rol de ADMIN.
   */
  @GetMapping("/ids")
  @PreAuthorize("isAuthenticated()")
  public Map<String, Long> ids(@RequestParam(required = false) Long userId,
                               Authentication auth) {
    Long uid = userId;
    if (uid == null && auth != null) {
      try {
        // En nuestro filtro JWT, el 'sub' (userId) queda en auth.getName()
        uid = Long.parseLong(auth.getName());
      } catch (Exception ignored) {}
    }
    if (uid == null) throw new IllegalArgumentException("userId es requerido");

    Long alumnoId = alumnos.findByUserId(uid).map(Alumno::getId).orElse(null);
    Long docenteId = docentes.findByUserId(uid).map(Docente::getId).orElse(null);
    Long adminId  = admins.findByUserId(uid).map(Administrador::getId).orElse(null);

    Map<String, Long> out = new HashMap<>();
    out.put("alumnoId", alumnoId);
    out.put("docenteId", docenteId);
    out.put("adminId",  adminId);
    return out;
  }
}
