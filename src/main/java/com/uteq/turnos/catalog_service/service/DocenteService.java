package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.dto.DocenteCreateRequest;
import com.uteq.turnos.catalog_service.dto.DocenteUpdateRequest;
import com.uteq.turnos.catalog_service.error.ConflictException;
import com.uteq.turnos.catalog_service.error.DuplicateException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.Division;
import com.uteq.turnos.catalog_service.model.Docente;
import com.uteq.turnos.catalog_service.repo.AlumnoRepo;
import com.uteq.turnos.catalog_service.repo.AdministradorRepo;
import com.uteq.turnos.catalog_service.repo.DivisionRepo;
import com.uteq.turnos.catalog_service.repo.DocenteRepo;
import jakarta.transaction.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class DocenteService {

  private final DocenteRepo repo;
  private final DivisionRepo divisionRepo;
  private final AdministradorRepo adminRepo;
  private final AlumnoRepo alumnoRepo;

  public DocenteService(DocenteRepo repo, DivisionRepo divisionRepo, AdministradorRepo adminRepo, AlumnoRepo alumnoRepo) {
    this.repo = repo;
    this.divisionRepo = divisionRepo;
    this.adminRepo = adminRepo;
    this.alumnoRepo = alumnoRepo;
  }

  public List<Docente> list(){ return repo.findAll(); }

  public Docente create(DocenteCreateRequest req){
    if (req == null || req.userId() == null) throw new IllegalArgumentException("userId es requerido");
    if (req.divisionId() == null) throw new IllegalArgumentException("divisionId es requerido");

    if (repo.existsByUserId(req.userId())) throw new DuplicateException("El usuario ya es DOCENTE");
    // Exclusividad de rol:
    if (adminRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es ADMIN");
    if (alumnoRepo.existsByUserId(req.userId())) throw new ConflictException("El usuario ya es ALUMNO");

    Division div = divisionRepo.findById(req.divisionId())
        .orElseThrow(() -> new NotFoundException("División no existe"));

    Docente d = Docente.builder()
        .userId(req.userId())
        .noEmpleado(req.noEmpleado())
        .division(div)
        .activo(req.activo() != null ? req.activo() : Boolean.TRUE)
        .build();

    return repo.save(d);
  }

  public Docente update(Long id, DocenteUpdateRequest req){
    Docente d = repo.findById(id).orElseThrow(() -> new NotFoundException("Docente no encontrado"));
    if (req.noEmpleado()!=null) d.setNoEmpleado(req.noEmpleado());
    if (req.activo()!=null) d.setActivo(req.activo());
    if (req.divisionId()!=null) {
      Division div = divisionRepo.findById(req.divisionId())
          .orElseThrow(() -> new NotFoundException("División no existe"));
      d.setDivision(div);
    }
    return repo.save(d);
  }

  public void delete(Long id){
    try {
      repo.deleteById(id);
    } catch (EmptyResultDataAccessException ignored) {
    } catch (DataIntegrityViolationException e) {
      // p.ej. tiene cubículo o MateriaDocente
      throw new ConflictException("No se puede eliminar: tiene relaciones asociadas (cubículo o materias)");
    }
  }

public List<Docente> listByDivision(Long divisionId) {
    if (divisionId == null) return List.of();
    // Ajusta a tu repo real:
    return repo.findByDivision_Id(divisionId);
    // return repo.findByDivisionId(divisionId);
  }  

}
