package com.TrackFit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Rol;
import com.TrackFit.repository.RolRepository;

import lombok.RequiredArgsConstructor;

/**
 * Servicio para la gestión y consulta de roles (HU01).
 * 
 * @author danie
 */
@Service
@RequiredArgsConstructor
public class RolService {

    private final RolRepository rolRepository;

    @Transactional(readOnly = true)
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRol(Integer idRol) {
        return rolRepository.findById(idRol);
    }

    @Transactional(readOnly = true)
    public Optional<Rol> getRolPorNombre(String rol) {
        return rolRepository.findByRol(rol);
    }
}

