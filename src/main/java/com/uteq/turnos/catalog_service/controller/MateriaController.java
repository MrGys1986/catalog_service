package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.dto.MateriaDTO;
import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.model.Docente;
import com.uteq.turnos.catalog_service.model.Materia;
import com.uteq.turnos.catalog_service.repo.DivisionRepo;
import com.uteq.turnos.catalog_service.repo.MateriaRepo;
import com.uteq.turnos.catalog_service.service.DocenteService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/materias")
public class MateriaController {

  private final MateriaRepo materias;
  private final DivisionRepo divisiones;
  private final DocenteService docentes;

  public MateriaController(MateriaRepo materias, DivisionRepo divisiones, DocenteService docentes) {
    this.materias = materias;
    this.divisiones = divisiones;
    this.docentes = docentes;
  }

  /** Lista general o filtrada por división. */
  @GetMapping(produces = "application/json")
  public Object list(@RequestParam(required = false) Long divisionId) {
    if (divisionId != null) {
      List<Materia> list = materias.findByDivisionId(divisionId);
      return list.stream().map(MateriaDTO::from).toList();   // ← para AsignarMaterias.jsx
    }
    return materias.findAllFetchDivision();                   // ← 👈 evita 500 en Materias.jsx
  }

  /** Crear */
  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public Materia create(@RequestBody Materia body) {
    if (body.getDivision() == null || body.getDivision().getId() == null) {
      throw new IllegalArgumentException("division.id es requerido");
    }
    Division d = divisiones.findById(body.getDivision().getId())
        .orElseThrow(() -> new IllegalArgumentException("División no encontrada"));

    if (body.getClave() != null) body.setClave(body.getClave().trim());

    Materia m = new Materia();
    m.setNombre(body.getNombre());
    m.setClave(body.getClave());
    m.setDivision(d);
    return materias.save(m);
  }

  /** Actualizar */
  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public Materia update(@PathVariable Long id, @RequestBody Materia body) {
    Materia m = materias.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada"));

    if (body.getNombre() != null) m.setNombre(body.getNombre());
    if (body.getClave() != null) m.setClave(body.getClave().trim());

    if (body.getDivision() != null && body.getDivision().getId() != null) {
      Division d = divisiones.findById(body.getDivision().getId())
          .orElseThrow(() -> new IllegalArgumentException("División no encontrada"));
      m.setDivision(d);
    }
    return materias.save(m);
  }

  /** Eliminar */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public void delete(@PathVariable Long id) {
    materias.deleteById(id);
  }

 /** Lista de materias para selects (solo autenticado) */
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public List<Materia> list() {
    return materias.findAll();
  }

  /** Detalle de una materia (necesario para fallback por división) */
  @GetMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public Materia get(@PathVariable Long id) {
    return materias.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));
  }

  /**
   * Docentes que pueden impartir la materia.
   * Implementado por división (clave para tu vista SolicitarTurno).
   */
  @GetMapping("/{id}/docentes")
  @PreAuthorize("hasAnyRole('ADMIN','DOCENTE','ALUMNO')")
  public List<Docente> docentesDeMateria(@PathVariable Long id) {
    Materia m = materias.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada"));

    Long divisionId = (m.getDivision() != null) ? m.getDivision().getId() : null;
    if (divisionId == null) return List.of();

    return docentes.listByDivision(divisionId);
  }

}
