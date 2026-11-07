package com.TrackFit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Progreso;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.ProgresoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgresoService {

    private final ProgresoRepository progresoRepository;

    @Transactional(readOnly = true)
    public List<Progreso> getProgresos() {
        return progresoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Progreso> getProgreso(Integer idProgreso) {
        return progresoRepository.findById(idProgreso);
    }

    @Transactional(readOnly = true)
    public List<Progreso> getProgresosPorCliente(Usuario cliente) {
        return progresoRepository.findByClienteOrderByFechaDesc(cliente);
    }

    @Transactional(readOnly = true)
    public List<Progreso> getProgresosPorCliente(String cedula) {
        return progresoRepository.findByClienteCedulaOrderByFechaDesc(cedula);
    }

    @Transactional
    public Progreso save(Progreso progreso) {
        return progresoRepository.save(progreso);
    }

    @Transactional
    public void delete(Integer idProgreso) {
        progresoRepository.deleteById(idProgreso);
    }
}