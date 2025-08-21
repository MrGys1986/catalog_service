package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="alumnos", uniqueConstraints=@UniqueConstraint(name="uq_alumno_noctrl", columnNames="no_control"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Alumno {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false, unique=true) private Long userId; // ID del Auth
  @Column(name="no_control", nullable=false, length=32) private String noControl;
  @Column(nullable=false) private Boolean activo = true;
}
