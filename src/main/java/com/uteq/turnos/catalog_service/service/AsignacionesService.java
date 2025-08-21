package com.uteq.turnos.catalog_service.service;

import com.uteq.turnos.catalog_service.error.ConflictException;
import com.uteq.turnos.catalog_service.error.NotFoundException;
import com.uteq.turnos.catalog_service.model.*;
import com.uteq.turnos.catalog_service.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AsignacionesService {
  private final MateriaRepo materias;
  private final DocenteRepo docentes;
  private final AlumnoRepo alumnos;
  private final CubiculoRepo cubiculos;
  private final MateriaDocenteRepo mdRepo;
  private final DocenteCubiculoRepo dcRepo;
  private final AlumnoMateriaRepo amRepo;

  public AsignacionesService(MateriaRepo materias, DocenteRepo docentes, AlumnoRepo alumnos,
                             CubiculoRepo cubiculos, MateriaDocenteRepo mdRepo,
                             DocenteCubiculoRepo dcRepo, AlumnoMateriaRepo amRepo) {
    this.materias = materias;
    this.docentes = docentes;
    this.alumnos = alumnos;
    this.cubiculos = cubiculos;
    this.mdRepo = mdRepo;
    this.dcRepo = dcRepo;
    this.amRepo = amRepo;
  }

  public MateriaDocente asignarDocente(Long materiaId, Long docenteId){
    Materia m = materias.findById(materiaId).orElseThrow(() -> new NotFoundException("Materia no encontrada"));
    Docente d = docentes.findById(docenteId).orElseThrow(() -> new NotFoundException("Docente no encontrado"));
    if (mdRepo.existsByMateriaId(materiaId)) throw new ConflictException("La materia ya tiene docente asignado");
    return mdRepo.save(MateriaDocente.builder().materia(m).docente(d).build());
  }

  public void quitarDocente(Long materiaId){
    MateriaDocente md = mdRepo.findByMateriaId(materiaId).orElseThrow(() -> new NotFoundException("Asignación no existe"));
    mdRepo.delete(md);
  }

  public List<MateriaDocente> materiasDeDocente(Long docenteId){
    return mdRepo.findByDocenteId(docenteId);
  }

  public DocenteCubiculo asignarCubiculo(Long docenteId, Long cubiculoId){
    Docente d = docentes.findById(docenteId).orElseThrow(() -> new NotFoundException("Docente no encontrado"));
    Cubiculo c = cubiculos.findById(cubiculoId).orElseThrow(() -> new NotFoundException("Cubículo no encontrado"));
    if (dcRepo.findByDocenteId(docenteId).isPresent()) throw new ConflictException("Docente ya tiene cubículo");
    if (dcRepo.findByCubiculoId(cubiculoId).isPresent()) throw new ConflictException("Cubículo ocupado");
    return dcRepo.save(DocenteCubiculo.builder().docente(d).cubiculo(c).build());
  }

  public void liberarCubiculo(Long docenteId){
    DocenteCubiculo dc = dcRepo.findByDocenteId(docenteId).orElseThrow(() -> new NotFoundException("No hay asignación"));
    dcRepo.delete(dc);
  }

  public AlumnoMateria inscribir(Long alumnoId, Long materiaId){
    Alumno a = alumnos.findById(alumnoId).orElseThrow(() -> new NotFoundException("Alumno no encontrado"));
    Materia m = materias.findById(materiaId).orElseThrow(() -> new NotFoundException("Materia no encontrada"));
    if (amRepo.existsByAlumnoIdAndMateriaId(alumnoId, materiaId)) throw new ConflictException("Ya inscrito");
    return amRepo.save(AlumnoMateria.builder().alumno(a).materia(m).build());
  }

  @Transactional
  public void desinscribir(Long alumnoId, Long materiaId){
    if (!amRepo.existsByAlumnoIdAndMateriaId(alumnoId, materiaId)) {
      throw new NotFoundException("Inscripción no existe");
    }
    amRepo.deleteByAlumnoIdAndMateriaId(alumnoId, materiaId);
  }

  public List<AlumnoMateria> materiasDeAlumno(Long alumnoId){
    return amRepo.findByAlumnoId(alumnoId);
  }

  /* ===================== NUEVO: requerido por CubiculoController ===================== */

  /** Devuelve el/los docentes asignados al cubículo.
   * En tu modelo actual (1:1), regresará 0 o 1 elemento. */
  public List<Docente> docentesDeCubiculo(Long cubiculoId) {
    // valida que exista el cubículo
    cubiculos.findById(cubiculoId)
        .orElseThrow(() -> new NotFoundException("Cubículo no encontrado"));

    return dcRepo.findByCubiculoId(cubiculoId)
        .map(dc -> List.of(dc.getDocente()))
        .orElseGet(List::of);
  }

  /** Reemplaza la asignación de docentes del cubículo.
   * Mantengo tu restricción 1:1: si vienen >1 IDs, lanzo conflicto. */
  @Transactional
  public void setDocentesDeCubiculo(Long cubiculoId, List<Long> docenteIds) {
    Cubiculo c = cubiculos.findById(cubiculoId)
        .orElseThrow(() -> new NotFoundException("Cubículo no encontrado"));

    // Limpia asignación actual (si existe)
    dcRepo.findByCubiculoId(cubiculoId).ifPresent(dcRepo::delete);

    // Si la lista viene vacía o null, queda sin docente
    if (docenteIds == null || docenteIds.isEmpty()) return;

    // Modelo actual es 1:1
    if (docenteIds.size() > 1) {
      throw new ConflictException("Solo se permite un docente por cubículo");
    }

    Long docenteId = docenteIds.get(0);
    Docente d = docentes.findById(docenteId)
        .orElseThrow(() -> new NotFoundException("Docente no encontrado"));

    // Asegura que el docente no esté ya asignado a otro cubículo
    dcRepo.findByDocenteId(docenteId).ifPresent(existing -> {
      if (!existing.getCubiculo().getId().equals(cubiculoId)) {
        throw new ConflictException("Docente ya tiene cubículo");
      }
    });

    dcRepo.save(DocenteCubiculo.builder().docente(d).cubiculo(c).build());
  }
}
