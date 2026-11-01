package com.TrackFit.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Rol;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.ClienteRepository;
import com.TrackFit.repository.RolRepository;
import com.TrackFit.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de Clientes / Socios desde el panel de Recepción (HU03).
 * Reutiliza la entidad Usuario garantizando que siempre opere con el rol 'CLIENTE'.
 * 
 * @author danie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Usuario> getClientes(boolean soloActivos) {
        if (soloActivos) {
            return clienteRepository.findClientesActivos();
        } else {
            return clienteRepository.findAllClientes();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getCliente(String cedula) {
        return clienteRepository.findClienteByCedula(cedula);
    }

    @Transactional(readOnly = true)
    public List<Usuario> buscarClientes(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return clienteRepository.findAllClientes();
        }
        return clienteRepository.buscarClientes(filtro.trim());
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
    public Usuario saveCliente(Usuario cliente, boolean esEdicion) {
        if (!esEdicion) {
            // Garantizar que en creación se le asigne el rol CLIENTE
            Rol rolCliente = rolRepository.findByRol("CLIENTE")
                    .orElseThrow(() -> new IllegalStateException("El rol CLIENTE no está configurado en la base de datos."));

            if (cliente.getRoles() == null) {
                cliente.setRoles(new ArrayList<>());
            }
            if (!cliente.getRoles().contains(rolCliente)) {
                cliente.getRoles().add(rolCliente);
            }
            cliente.setActivo(true);
        } else {
            // En edición, preservar los roles existentes
            usuarioRepository.findById(cliente.getCedula()).ifPresent(original -> {
                cliente.setRoles(original.getRoles());
            });
        }

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void deleteCliente(String cedula) {
        if (!clienteRepository.existsById(cedula)) {
            throw new IllegalArgumentException("El cliente con cédula " + cedula + " no existe.");
        }
        try {
            clienteRepository.deleteById(cedula);
            clienteRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("No fue posible eliminar el cliente {} porque tiene membresías, pagos o reservas asociadas", cedula, e);
            throw e;
        }
    }

    @Transactional
    public void cambiarEstado(String cedula) {
        clienteRepository.findById(cedula).ifPresent(c -> {
            c.setActivo(!c.isActivo());
            clienteRepository.save(c);
        });
    }
}

