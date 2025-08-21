package com.uteq.turnos.catalog_service.controller;

import com.uteq.turnos.catalog_service.model.Cubiculo;
import com.uteq.turnos.catalog_service.model.Docente;
import com.uteq.turnos.catalog_service.service.AsignacionesService;
import com.uteq.turnos.catalog_service.service.CubiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/cubiculos")
public class CubiculoController {
  private final CubiculoService svc;
  private final AsignacionesService asignaciones;

  public CubiculoController(CubiculoService s, AsignacionesService a){
    this.svc = s; this.asignaciones = a;
  }

  @GetMapping public List<Cubiculo> list(){ return svc.list(); }
  @GetMapping("/{id}") public Cubiculo get(@PathVariable Long id){ return svc.get(id); }
  @PostMapping @PreAuthorize("hasRole('ADMIN')") public Cubiculo create(@RequestBody Cubiculo c){ return svc.create(c); }
  @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')") public Cubiculo update(@PathVariable Long id, @RequestBody Cubiculo c){ return svc.update(id,c); }


  @GetMapping("/{id}/docentes")
  public List<Docente> docentesDeCubiculo(@PathVariable Long id) {
    return asignaciones.docentesDeCubiculo(id);
  }

  @PutMapping("/{id}/docentes")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> setDocentesDeCubiculo(@PathVariable Long id, @RequestBody List<Long> docenteIds) {
    asignaciones.setDocentesDeCubiculo(id, docenteIds);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id){
    svc.delete(id); // (ya corregido en tu versión anterior)
    return ResponseEntity.noContent().build();
  }
}
