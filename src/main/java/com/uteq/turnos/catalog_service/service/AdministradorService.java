package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.dto.AdministradorCreateRequest;
import com.uteq.turnos.catalog_service.dto.AdministradorUpdateRequest;
import com.uteq.turnos.catalog_service.error.ConflictException;
import com.uteq.turnos.catalog_service.error.DuplicateException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Administrador;
import com.uteq.turnos.catalog_service.repo.AdministradorRepo;
import com.uteq.turnos.catalog_service.repo.AlumnoRepo;
import com.uteq.turnos.catalog_service.repo.DocenteRepo;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class AdministradorService {

  private final AdministradorRepo repo;
  private final DocenteRepo docenteRepo;
  private final AlumnoRepo alumnoRepo;

  public AdministradorService(AdministradorRepo repo, DocenteRepo docenteRepo, AlumnoRepo alumnoRepo) {
    this.repo = repo;
    this.docenteRepo = docenteRepo;
    this.alumnoRepo = alumnoRepo;
  }

  public List<Administrador> list(){ return repo.findAll(); }

  public Administrador create(AdministradorCreateRequest req) {
    if (req == null || req.userId() == null) throw new IllegalArgumentException("userId es requerido");
    if (repo.existsByUserId(req.userId())) throw new DuplicateException("El usuario ya es ADMIN");
    // Exclusividad de rol:
    if (docenteRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es DOCENTE");
    if (alumnoRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es ALUMNO");

    Administrador a = Administrador.builder()
        .userId(req.userId())
        .noTrabajador(req.noTrabajador())
        .activo(req.activo() != null ? req.activo() : Boolean.TRUE)
        .build();
    return repo.save(a);
  }

  public Administrador update(Long id, AdministradorUpdateRequest req) {
    Administrador a = repo.findById(id).orElseThrow(() -> new NotFoundException("Administrador no encontrado"));
    if (req.noTrabajador() != null) a.setNoTrabajador(req.noTrabajador());
    if (req.activo() != null) a.setActivo(req.activo());
    return repo.save(a);
  }

  public void delete(Long id) {
    try {
      repo.deleteById(id);
    } catch (EmptyResultDataAccessException ignored) {
    } catch (DataIntegrityViolationException e) {
      throw new ConflictException("No se puede eliminar: tiene relaciones asociadas");
    }
  }
  
}
