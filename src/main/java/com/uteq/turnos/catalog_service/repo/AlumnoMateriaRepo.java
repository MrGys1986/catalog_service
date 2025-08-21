package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.AlumnoMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

public interface AlumnoMateriaRepo extends JpaRepository<AlumnoMateria, Long> {

  // Listar inscripciones de un alumno
  List<AlumnoMateria> findByAlumnoId(Long alumnoId);

  // Para verificar si ya está inscrito a una materia
  boolean existsByAlumnoIdAndMateriaId(Long alumnoId, Long materiaId);

  Optional<AlumnoMateria> findByAlumnoIdAndMateriaId(Long alumnoId, Long materiaId);

  // Borrado directo por alumno+materia
  @Modifying
  @Transactional
  void deleteByAlumnoIdAndMateriaId(Long alumnoId, Long materiaId);

  // ⚠️ Si quieres buscar por el userId del alumno, usa la ruta anidada:
  // (esto es opcional; inclúyelo solo si lo necesitas)
  List<AlumnoMateria> findByAlumnoUserId(Long userId);
}
