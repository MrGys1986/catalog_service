package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.error.DuplicateException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.model.Materia;
import com.uteq.turnos.catalog_service.repo.DivisionRepo;
import com.uteq.turnos.catalog_service.repo.MateriaRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MateriaService {
  private final MateriaRepo materias;
  private final DivisionRepo divisiones;

  public MateriaService(MateriaRepo m, DivisionRepo d) {
    this.materias = m; this.divisiones = d;
  }

  public Materia create(Materia in) {
    if (in.getDivision() == null || in.getDivision().getId() == null)
      throw new NotFoundException("Division no especificada");

    // Normalización opcional
    in.setClave(in.getClave() == null ? null : in.getClave().trim().toUpperCase());

    if (materias.existsByClave(in.getClave()))
      throw new DuplicateException("Clave de materia duplicada");

    Division div = divisiones.findById(in.getDivision().getId())
        .orElseThrow(() -> new NotFoundException("Division no encontrada"));

    in.setDivision(div);
    return materias.save(in);
  }

  public Materia update(Long id, Materia in) {
    Materia m = materias.findById(id)
        .orElseThrow(() -> new NotFoundException("Materia no encontrada"));

    String nuevaClave = in.getClave() == null ? null : in.getClave().trim().toUpperCase();

    if (!m.getClave().equals(nuevaClave) && materias.existsByClave(nuevaClave))
      throw new DuplicateException("Clave duplicada");

    Division div = divisiones.findById(in.getDivision().getId())
        .orElseThrow(() -> new NotFoundException("Division no encontrada"));

    m.setNombre(in.getNombre());
    m.setClave(nuevaClave);
    m.setDivision(div);
    return materias.save(m);
  }

  public List<Materia> byDivision(Long divisionId) { return materias.findByDivisionId(divisionId); }
  public Materia get(Long id) { return materias.findById(id).orElseThrow(() -> new NotFoundException("Materia no encontrada")); }
  public List<Materia> list() { return materias.findAll(); }
  public void delete(Long id) { materias.deleteById(id); }
}
