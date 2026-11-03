/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

//Clase que representa las reservas de actividades del gimnasio
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "reserva")
public class Reserva implements Serializable {

    private static final long serialVersionUID = 1L;

    //Identificador único de la reserva
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer idReserva;

    //Cliente que realiza la reserva
    @ManyToOne
    @JoinColumn(name = "cedula_cliente", nullable = false)
    private Usuario cliente;

    //Horario que el cliente desea reservar
    @ManyToOne
    @JoinColumn(name = "id_horario", nullable = false)
    private Horario horario;

    //Estado de la reserva: Confirmada o Cancelada
    @Column(name = "estado", nullable = false)
    private String estado = "Confirmada";

    //Fecha en que se realizó la reserva
    @Column(name = "fecha_reserva", insertable = false, updatable = false)
    private LocalDateTime fechaReserva;

    //Fecha de la última modificación
    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
}
