package com.uteq.turnos.catalog_service.dto;

public record DocenteCreateRequest(Long userId, String noEmpleado, Long divisionId, Boolean activo) {}
