/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.TrackFit.repository;

import com.TrackFit.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

//Repositorio encargado de acceder a la tabla reserva
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    //Cuenta las reservas de un horario según su estado
    long countByHorarioIdHorarioAndEstado(
            Integer idHorario,
            String estado);

}
