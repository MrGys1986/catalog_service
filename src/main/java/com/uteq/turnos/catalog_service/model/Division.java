package com.uteq.turnos.catalog_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler","materias"})
@Entity
@Table(name="divisiones", uniqueConstraints=@UniqueConstraint(name="uq_div_clave", columnNames="clave"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Division {
  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable=false)
  private String nombre;

  @NotBlank
  @Column(nullable=false, unique=true, length=32)
  private String clave;

  // Nota: no definimos la colección materias aquí; si la agregas en el futuro,
  // la anotación @JsonIgnoreProperties ya previene recursión al serializar.
}
