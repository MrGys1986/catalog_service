// src/main/java/com/uteq/turnos/catalog_service/dto/DocenteDto.java
package com.uteq.turnos.catalog_service.dto;

public record DocenteUpdateRequest(String noEmpleado, Long divisionId, Boolean activo) {}
