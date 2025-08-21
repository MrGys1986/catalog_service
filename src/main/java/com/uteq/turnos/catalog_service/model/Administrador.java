package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="administradores", indexes=@Index(name="ix_admin_user", columnList="user_id", unique=true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Administrador {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false, unique=true) private Long userId; // ID del Auth
  @Column(name="no_trabajador", length=32) private String noTrabajador;
  @Column(nullable=false) private Boolean activo = true;
}
