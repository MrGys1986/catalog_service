package com.uteq.turnos.catalog_service.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(
  name = "materias",
  uniqueConstraints = {
    @UniqueConstraint(name = "uk_materia_clave", columnNames = "clave")
  }
)
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Materia {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 150)
  private String nombre;

  @Column(nullable = false, length = 64, unique = true)
  private String clave;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "division_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_materia_division")
  )
  @JsonIgnoreProperties({"hibernateLazyInitializer","handler","materias"})
  private Division division;

  // getters/setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getNombre() { return nombre; }
  public void setNombre(String nombre) { this.nombre = nombre; }

  public String getClave() { return clave; }
  public void setClave(String clave) { this.clave = clave; }

  public Division getDivision() { return division; }
  public void setDivision(Division division) { this.division = division; }
}
