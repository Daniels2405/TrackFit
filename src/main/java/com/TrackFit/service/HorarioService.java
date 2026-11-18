/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.service;

import com.TrackFit.domain.Horario;
import com.TrackFit.repository.HorarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TrackFit.repository.ReservaRepository;
import java.time.LocalDate;

//Clase encargada de gestionar los horarios de las actividades
@Service
@RequiredArgsConstructor
public class HorarioService {

    //Repositorio que permite acceder a la tabla horario
    private final HorarioRepository horarioRepository;

    //Repositorio que permite consultar las reservas de los horarios
    private final ReservaRepository reservaRepository;

    //Servicio que permite consultar las actividades del gimnasio
    private final ActividadService actividadService;

    //Método que obtiene todos los horarios registrados
    @Transactional(readOnly = true)
    public List<Horario> getHorarios() {
        return horarioRepository.findAll();
    }

    //Método que busca un horario por su identificador
    @Transactional(readOnly = true)
    public Horario getHorario(Integer idHorario) {
        return horarioRepository.findById(idHorario).orElse(null);
    }

    //Método que registra o actualiza un horario
    @Transactional
    public void save(Horario horario) {

        //Verifica que se haya seleccionado una actividad
        if (horario.getActividad() == null
                || horario.getActividad().getIdActividad() == null) {
            throw new IllegalArgumentException(
                    "horario.error.actividad.obligatoria");
        }

        //Busca la actividad seleccionada en la base de datos
        var actividad = actividadService.getActividad(
                horario.getActividad().getIdActividad());

        //Verifica que la actividad exista
        if (actividad == null) {
            throw new IllegalArgumentException(
                    "horario.error.actividad.no.existe");
        }

        //Verifica que la actividad esté activa
        if (!Boolean.TRUE.equals(actividad.getActivo())) {
            throw new IllegalArgumentException(
                    "horario.error.actividad.inactiva");
        }

        //Asigna al horario la actividad encontrada
        horario.setActividad(actividad);

        //Verifica que se haya ingresado una fecha
        if (horario.getFecha() == null) {
            throw new IllegalArgumentException(
                    "horario.error.fecha.obligatoria");
        }

        //Verifica que la fecha no sea anterior al día actual
        if (horario.getFecha().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "horario.error.fecha.pasada");
        }

        //Verifica que se hayan ingresado ambas horas
        if (horario.getHoraInicio() == null || horario.getHoraFin() == null) {
            throw new IllegalArgumentException(
                    "horario.error.horas.obligatorias");
        }

        //Verifica que la hora final sea posterior a la inicial
        if (!horario.getHoraFin().isAfter(horario.getHoraInicio())) {
            throw new IllegalArgumentException(
                    "horario.error.hora.invalida");
        }

        //Verifica que el cupo máximo sea mayor que cero
        if (horario.getCupoMaximo() == null || horario.getCupoMaximo() <= 0) {
            throw new IllegalArgumentException(
                    "horario.error.cupo.invalido");
        }

        //Verifica si ya existe un horario para la misma actividad, fecha y hora de inicio
        boolean existeHorario
                = horarioRepository.existsByActividadIdActividadAndFechaAndHoraInicio(
                        horario.getActividad().getIdActividad(),
                        horario.getFecha(),
                        horario.getHoraInicio());

        //Si el horario ya existe, no permite registrarlo
        if (existeHorario) {
            throw new IllegalArgumentException(
                    "horario.error.duplicado");
        }

//Obtiene los horarios que ya están registrados
        List<Horario> horarios = horarioRepository.findAll();

//Recorre los horarios existentes
        for (Horario h : horarios) {

            //Verifica que sea la misma actividad y fecha
            if (h.getActividad().getIdActividad().equals(
                    horario.getActividad().getIdActividad())
                    && h.getFecha().equals(horario.getFecha())) {

                //Comprueba si las horas se traslapan
                if (horario.getHoraInicio().isBefore(h.getHoraFin())
                        && horario.getHoraFin().isAfter(h.getHoraInicio())) {

                    //Impide guardar horarios que coincidan
                    throw new IllegalArgumentException(
                            "horario.error.traslape");
                }
            }
        }

        //Guarda el horario en la base de datos
        horarioRepository.save(horario);
    }

//Método que calcula los espacios disponibles de un horario
    @Transactional(readOnly = true)
    public int getEspaciosDisponibles(Horario horario) {

        //Cuenta únicamente las reservas confirmadas
        long reservasConfirmadas = reservaRepository.countByHorarioIdHorarioAndEstado(
                horario.getIdHorario(), "Confirmada");

        //Resta las reservas confirmadas al cupo máximo
        int disponibles = horario.getCupoMaximo() - (int) reservasConfirmadas;

        //Evita mostrar una cantidad negativa de espacios
        if (disponibles < 0) {
            disponibles = 0;
        }

        //Devuelve la cantidad de espacios disponibles
        return disponibles;
    }

}
