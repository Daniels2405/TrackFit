/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.service;

import com.TrackFit.domain.Reserva;
import com.TrackFit.domain.Horario;
import com.TrackFit.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TrackFit.domain.Usuario;

//Servicio encargado de validar y guardar las reservas
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HorarioService horarioService;

    //Método para registrar una nueva reserva
    @Transactional
    public void save(Reserva reserva) {

        //Verifica que se haya seleccionado un horario
        if (reserva.getHorario() == null
                || reserva.getHorario().getIdHorario() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un horario");
        }

        //Busca el horario registrado en la base de datos
        Horario horario = horarioService.getHorario(
                reserva.getHorario().getIdHorario());

        //Verifica que el horario exista
        if (horario == null) {
            throw new IllegalArgumentException(
                    "El horario seleccionado no existe");
        }

        //Consulta cuántos espacios quedan disponibles
        int disponibles
                = horarioService.getEspaciosDisponibles(horario);

        //Impide registrar una reserva cuando el cupo está lleno
        if (disponibles <= 0) {
            throw new IllegalArgumentException(
                    "No hay espacios disponibles para esta actividad");
        }

        //Asigna el horario existente a la reserva
        reserva.setHorario(horario);

        //Registra la reserva como confirmada
        reserva.setEstado("Confirmada");

        //Guarda la reserva en la base de datos
        reservaRepository.save(reserva);
    }

}
