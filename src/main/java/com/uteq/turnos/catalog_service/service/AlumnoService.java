package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.dto.AlumnoCreateRequest;
import com.uteq.turnos.catalog_service.dto.AlumnoUpdateRequest;
import com.uteq.turnos.catalog_service.error.ConflictException;
import com.uteq.turnos.catalog_service.error.DuplicateException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Alumno;
import com.uteq.turnos.catalog_service.repo.AlumnoRepo;
import com.uteq.turnos.catalog_service.repo.AdministradorRepo;
import com.uteq.turnos.catalog_service.repo.DocenteRepo;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AlumnoService {

  private final AlumnoRepo repo;
  private final DocenteRepo docenteRepo;
  private final AdministradorRepo adminRepo;

  public AlumnoService(AlumnoRepo repo, DocenteRepo docenteRepo, AdministradorRepo adminRepo) {
    this.repo = repo;
    this.docenteRepo = docenteRepo;
    this.adminRepo = adminRepo;
  }

  public List<Alumno> list(){ return repo.findAll(); }

  public Alumno create(AlumnoCreateRequest req){
    if (req == null || req.userId() == null) throw new IllegalArgumentException("userId es requerido");
    if (req.noControl() == null || req.noControl().isBlank()) throw new IllegalArgumentException("noControl es requerido");

    if (repo.findByUserId(req.userId()).isPresent()) throw new DuplicateException("El usuario ya es ALUMNO");
    if (repo.existsByNoControl(req.noControl())) throw new DuplicateException("El noControl ya existe");

    // Exclusividad de rol:
    if (docenteRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es DOCENTE");
    if (adminRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es ADMIN");

    Alumno a = Alumno.builder()
        .userId(req.userId())
        .noControl(req.noControl())
        .activo(req.activo() != null ? req.activo() : Boolean.TRUE)
        .build();
    return repo.save(a);
  }

  public Alumno update(Long id, AlumnoUpdateRequest req){
    Alumno a = repo.findById(id).orElseThrow(() -> new NotFoundException("Alumno no encontrado"));
    if (req.noControl()!=null) a.setNoControl(req.noControl());
    if (req.activo()!=null) a.setActivo(req.activo());
    return repo.save(a);
  }

  public void delete(Long id){
    try {
      repo.deleteById(id);
    } catch (EmptyResultDataAccessException ignored) {
    } catch (DataIntegrityViolationException e) {
      // p.ej. tiene AlumnoMateria
      throw new ConflictException("No se puede eliminar: tiene relaciones asociadas (inscripciones)");
    }
  }
}
