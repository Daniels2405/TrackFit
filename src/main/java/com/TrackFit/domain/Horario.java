/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

//Clase que representa los horarios de las actividades del gimnasio
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "horario")
public class Horario implements Serializable {

    private static final long serialVersionUID = 1L;

    //Identificador único del horario
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_horario")
    private Integer idHorario;

    //Actividad a la que pertenece el horario
    @ManyToOne
    @JoinColumn(name = "id_actividad", nullable = false)
    private Actividad actividad;

    //Fecha en la que se realizará la actividad
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;

    //Hora en la que inicia la actividad
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    //Hora en la que finaliza la actividad
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    //Cantidad máxima de participantes permitidos
    @Column(name = "cupo_maximo", nullable = false)
    private Integer cupoMaximo;

    //Entrenador asignado al horario (opcional)
    @ManyToOne
    @JoinColumn(name = "cedula_entrenador")
    private Usuario entrenador;

    //Fecha en la que se creó el horario
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    //Fecha de la última modificación del horario
    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
