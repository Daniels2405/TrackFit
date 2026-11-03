/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.TrackFit.repository;

import com.TrackFit.domain.Horario;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;

// Repositorio encargado de acceder a la tabla horario
public interface HorarioRepository extends JpaRepository<Horario, Integer> {

    // Verifica si ya existe un horario para la misma
    // actividad, fecha y hora de inicio
    boolean existsByActividadIdActividadAndFechaAndHoraInicio(
            Integer idActividad,
            LocalDate fecha,
            LocalTime horaInicio);

}
