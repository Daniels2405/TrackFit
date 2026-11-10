package com.TrackFit.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "progreso")
public class Progreso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_progreso")
    private Integer idProgreso;

    @ManyToOne
    @JoinColumn(name = "cedula_cliente")
    private Usuario cliente;

    @ManyToOne
    @JoinColumn(name = "cedula_entrenador")
    private Usuario entrenador;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha", nullable = false)
    @NotNull(message = "La fecha es obligatoria")
    private LocalDate fecha;

    @Column(name = "peso", nullable = false)
    @NotNull(message = "El peso es obligatorio")
    private BigDecimal peso;

    @Column(name = "porcentaje_grasa")
    private BigDecimal porcentajeGrasa;

    @Column(name = "observaciones", length = 500)
    @Size(max = 500)
    private String observaciones;

    @Column(name = "fecha_creacion",
            insertable = false,
            updatable = false)
    private LocalDateTime fechaCreacion;
}