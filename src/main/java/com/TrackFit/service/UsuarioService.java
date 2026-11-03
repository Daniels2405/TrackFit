package com.TrackFit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la administración general de usuarios del sistema (HU01).
 * 
 * @author danie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios(boolean activo) {
        if (activo) {
            return usuarioRepository.findByActivoTrue();
        } else {
            return usuarioRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuario(String cedula) {
        return usuarioRepository.findById(cedula);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    @Transactional(readOnly = true)
    public boolean existeCedula(String cedula) {
        return usuarioRepository.existsByCedula(cedula);
    }

    @Transactional(readOnly = true)
    public boolean existeUsername(String username, String cedulaActual) {
        if (cedulaActual == null || cedulaActual.isBlank()) {
            return usuarioRepository.existsByUsername(username);
        }
        return usuarioRepository.existsByUsernameAndCedulaNot(username, cedulaActual);
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo, String cedulaActual) {
        if (cedulaActual == null || cedulaActual.isBlank()) {
            return usuarioRepository.existsByCorreo(correo);
        }
        return usuarioRepository.existsByCorreoAndCedulaNot(correo, cedulaActual);
    }

    @Transactional
    public Usuario save(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public void delete(String cedula) {
        if (!usuarioRepository.existsById(cedula)) {
            throw new IllegalArgumentException("El usuario con cédula " + cedula + " no existe.");
        }
        try {
            usuarioRepository.deleteById(cedula);
            usuarioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("No fue posible eliminar el usuario con cédula {} por registros asociados", cedula, e);
            throw e;
        }
    }

    @Transactional
    public void cambiarEstado(String cedula) {
        usuarioRepository.findById(cedula).ifPresent(u -> {
            u.setActivo(!u.isActivo());
            usuarioRepository.save(u);
        });
    }
}

