// src/main/java/com/uteq/turnos/catalog_service/dto/AlumnoMateriaDTO.java
package com.uteq.turnos.catalog_service.dto;

import com.uteq.turnos.catalog_service.model.AlumnoMateria;

public record AlumnoMateriaDTO(Long id, MateriaDTO materia) {
  public static AlumnoMateriaDTO from(AlumnoMateria am) {
    return new AlumnoMateriaDTO(am.getId(), MateriaDTO.from(am.getMateria()));
  }
}
