package com.TrackFit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.Usuario;

/**
 * Repositorio especializado para Clientes (reutilizando la entidad Usuario con rol CLIENTE).
 * Diseñado para el centro de control de Recepción (HU03).
 * 
 * @author danie
 */
@Repository
public interface ClienteRepository extends JpaRepository<Usuario, String> {

    // Todos los usuarios que tienen asignado el rol CLIENTE
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.rol = 'CLIENTE'")
    List<Usuario> findAllClientes();

    // Clientes activos
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.rol = 'CLIENTE' AND u.activo = true")
    List<Usuario> findClientesActivos();

    // Búsqueda de clientes por coincidencia de nombre, apellidos, correo o cédula
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.rol = 'CLIENTE' AND (" +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
           "LOWER(u.apellido1) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
           "LOWER(u.correo) LIKE LOWER(CONCAT('%', :filtro, '%')) OR " +
           "u.cedula LIKE CONCAT('%', :filtro, '%'))")
    List<Usuario> buscarClientes(@Param("filtro") String filtro);

    // Obtener un cliente específico validando que posea el rol CLIENTE
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE u.cedula = :cedula AND r.rol = 'CLIENTE'")
    Optional<Usuario> findClienteByCedula(@Param("cedula") String cedula);
}

