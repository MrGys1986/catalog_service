package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.AlumnoMateria;
import com.uteq.turnos.catalog_service.model.DocenteCubiculo;
import com.uteq.turnos.catalog_service.model.MateriaDocente;
import com.uteq.turnos.catalog_service.service.AsignacionesService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/asignaciones")
public class AsignacionesController {
  private final AsignacionesService svc;
  public AsignacionesController(AsignacionesService s){ this.svc=s; }

  // ===== Materia ↔ Docente =====
  @PostMapping("/materias/{materiaId}/docente/{docenteId}") @PreAuthorize("hasRole('ADMIN')")
  public MateriaDocente asignarDocente(@PathVariable Long materiaId, @PathVariable Long docenteId){
    return svc.asignarDocente(materiaId, docenteId);
  }

  @DeleteMapping("/materias/{materiaId}/docente") @PreAuthorize("hasRole('ADMIN')")
  public void quitarDocente(@PathVariable Long materiaId){ svc.quitarDocente(materiaId); }

  // ===== Docente ↔ Cubículo =====
  @PostMapping("/docentes/{docenteId}/cubiculo/{cubiculoId}") @PreAuthorize("hasRole('ADMIN')")
  public DocenteCubiculo asignarCubiculo(@PathVariable Long docenteId, @PathVariable Long cubiculoId){
    return svc.asignarCubiculo(docenteId, cubiculoId);
  }

  @DeleteMapping("/docentes/{docenteId}/cubiculo") @PreAuthorize("hasRole('ADMIN')")
  public void liberarCubiculo(@PathVariable Long docenteId){ svc.liberarCubiculo(docenteId); }

  // ===== Alumno ↔ Materia =====
  @PostMapping("/alumnos/{alumnoId}/materias/{materiaId}") @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
  public AlumnoMateria inscribirMateria(@PathVariable Long alumnoId, @PathVariable Long materiaId) {
    return svc.inscribir(alumnoId, materiaId);
  }

  @DeleteMapping("/alumnos/{alumnoId}/materias/{materiaId}") @PreAuthorize("hasAnyRole('ADMIN','DOCENTE')")
  public void desinscribirMateria(@PathVariable Long alumnoId, @PathVariable Long materiaId) {
    svc.desinscribir(alumnoId, materiaId);
  }
}
