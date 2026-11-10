package com.TrackFit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.TrackFit.domain.Progreso;
import com.TrackFit.domain.Usuario;

public interface ProgresoRepository
        extends JpaRepository<Progreso, Integer> {

    List<Progreso> findByClienteOrderByFechaDesc(
            Usuario cliente);

    List<Progreso> findByClienteCedulaOrderByFechaDesc(
            String cedula);

    
}