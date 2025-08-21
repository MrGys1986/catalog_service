package com.uteq.turnos.catalog_service.dto;

public record AdministradorCreateRequest(Long userId, String noTrabajador, Boolean activo) {}
