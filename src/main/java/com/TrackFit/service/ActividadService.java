/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.service;

import com.TrackFit.domain.Actividad;
import com.TrackFit.repository.ActividadRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Clase encargada de gestionar las actividades del gimnasio
@Service
@RequiredArgsConstructor
public class ActividadService {

    //Repositorio que permite acceder a la tabla actividad
    private final ActividadRepository actividadRepository;

    //Método que obtiene todas las actividades registradas
    @Transactional(readOnly = true)
    public List<Actividad> getActividades() {
        return actividadRepository.findAll();
    }

    //Método que busca una actividad por su identificador
    @Transactional(readOnly = true)
    public Actividad getActividad(Integer idActividad) {
        return actividadRepository.findById(idActividad).orElse(null);
    }

    //Método que guarda una actividad y verifica que no esté repetida
    @Transactional
    public void save(Actividad actividad) {

        //Verifica que el nombre no esté vacío
        if (actividad.getNombre() == null
                || actividad.getNombre().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "actividad.error.nombre.obligatorio");
        }

        //Elimina los espacios al inicio y al final del nombre
        String nombre = actividad.getNombre().trim();
        actividad.setNombre(nombre);

        //Verifica si ya existe una actividad con ese nombre
        if (actividadRepository.existsByNombreIgnoreCase(nombre)) {

            throw new IllegalArgumentException(
                    "actividad.error.nombre.existe");
        }

        //Guarda la nueva actividad en la base de datos
        actividadRepository.save(actividad);
    }

}
