package com.TrackFit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.Usuario;

/**
 * Repositorio para la entidad Usuario (HU01 - Usuarios del sistema).
 * 
 * @author danie
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    // Búsqueda por nombre de usuario (para autenticación y credenciales en el siguiente avance)
    Optional<Usuario> findByUsername(String username);

    // Búsqueda por correo electrónico
    Optional<Usuario> findByCorreo(String correo);

    // Validaciones de existencia previa
    boolean existsByCedula(String cedula);
    boolean existsByUsername(String username);
    boolean existsByCorreo(String correo);

    // Validaciones para edición (excluyendo la cédula actual)
    boolean existsByUsernameAndCedulaNot(String username, String cedula);
    boolean existsByCorreoAndCedulaNot(String correo, String cedula);

    // Listar usuarios activos
    List<Usuario> findByActivoTrue();
}

