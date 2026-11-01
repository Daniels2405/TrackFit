package com.TrackFit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.Rol;

/**
 * Repositorio para la entidad Rol (HU01 - Roles del sistema).
 * 
 * @author danie
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {

    // Búsqueda de rol por su denominación (ADMINISTRADOR, RECEPCION, ENTRENADOR, CLIENTE)
    Optional<Rol> findByRol(String rol);
}

