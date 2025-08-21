package com.uteq.turnos.catalog_service.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="docentes", indexes=@Index(name="ix_doc_user", columnList="user_id", unique=true))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Docente {
  @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
  @Column(name="user_id", nullable=false, unique=true) private Long userId;
  @Column(name="no_empleado", length=32) private String noEmpleado;
  @ManyToOne(optional=false) @JoinColumn(name="division_id", nullable=false)
  private Division division;
  @Column(nullable=false) private Boolean activo = true;
}
