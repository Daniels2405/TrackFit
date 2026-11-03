package com.TrackFit.domain;
import java.io.Serializable;
import java.math.BigDecimal;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "rutina_ejercicio")
public class RutinaEjercicio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_ejercicio")
    private Integer idRutinaEjercicio;

    @ManyToOne
    @JoinColumn(name = "id_rutina", nullable = false)
    private Rutina rutina;

    @ManyToOne
    @JoinColumn(name = "id_ejercicio", nullable = false)
    private Ejercicio ejercicio;

    @Column(name = "series", nullable = false)
    @NotNull(message = "Las series no pueden estar vacías")
    @Min(value = 1, message = "Debe haber al menos 1 serie")
    private Integer series;

    @Column(name = "repeticiones", nullable = false)
    @NotNull(message = "Las repeticiones no pueden estar vacías")
    @Min(value = 1, message = "Debe haber al menos 1 repetición")
    private Integer repeticiones;

    @Column(name = "peso", precision = 6, scale = 2)
    @DecimalMin(value = "0.00", message = "El peso no puede ser negativo")
    private BigDecimal peso;

    @Column(name = "observaciones", length = 255)
    @Size(max = 255)
    private String observaciones;
}
