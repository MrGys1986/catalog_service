package com.uteq.turnos.catalog_service.repo;

import com.uteq.turnos.catalog_service.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AlumnoRepo extends JpaRepository<Alumno, Long> {

  // ya lo usas en servicios para validar duplicados por userId
  boolean existsByUserId(Long userId);

  // útil si quieres obtener el registro
  Optional<Alumno> findByUserId(Long userId);

  // ya lo usas para noControl
  boolean existsByNoControl(String noControl);
}

