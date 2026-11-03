/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.TrackFit.repository;

import com.TrackFit.domain.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActividadRepository extends JpaRepository<Actividad, Integer> {
    
    //Verifica si ya existe una actividad con el mismo nombre
    boolean existsByNombreIgnoreCase(String nombre);


}
