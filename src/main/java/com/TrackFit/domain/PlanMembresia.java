package com.TrackFit.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author danie
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "plan_membresia")
public class PlanMembresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_plan")
    private Integer idPlan;

    @Column(name = "nombre", unique = true, nullable = false, length = 30)
    @NotBlank(message = "El nombre del plan no puede estar vacío")
    @Size(max = 30)
    private String nombre;

    @Column(name = "precio", precision = 12, scale = 2, nullable = false)
    @NotNull(message = "El precio no puede ser nulo")
    @DecimalMin(value = "0.00", inclusive = true, message = "El precio debe ser mayor o igual a 0")
    private BigDecimal precio;

    @Column(name = "duracion_dias", nullable = false)
    @NotNull(message = "La duración en días no puede estar vacía")
    @Min(value = 1, message = "La duración debe ser de al menos 1 día")
    private Integer duracionDias;

    @Column(name = "anticipacion_reserva_dias", nullable = false)
    @NotNull(message = "La anticipación de reserva no puede estar vacía")
    @Min(value = 0, message = "La anticipación de reserva debe ser mayor o igual a 0")
    private Integer anticipacionReservaDias;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    private boolean activo;

    @OneToMany(mappedBy = "planMembresia")
    private List<PlanBeneficio> beneficios;

    @OneToMany(mappedBy = "planMembresia")
    private List<Membresia> membresias;
}
