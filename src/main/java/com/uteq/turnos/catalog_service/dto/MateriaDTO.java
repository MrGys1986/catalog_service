// src/main/java/com/uteq/turnos/catalog_service/dto/MateriaDTO.java
package com.uteq.turnos.catalog_service.dto;

import com.uteq.turnos.catalog_service.model.Materia;

public record MateriaDTO(Long id, String clave, String nombre) {
  public static MateriaDTO from(Materia m) {
    return new MateriaDTO(m.getId(), m.getClave(), m.getNombre());
  }
}
