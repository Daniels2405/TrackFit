package com.TrackFit.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Membresia;
import com.TrackFit.domain.PlanMembresia;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.MembresiaRepository;
import com.TrackFit.repository.PlanMembresiaRepository;
import com.TrackFit.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión y asignación de Membresías (HU05 y HU06).
 * Calcula automáticamente la fecha de vencimiento según la duración del plan.
 * 
 * @author danie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PlanMembresiaRepository planMembresiaRepository;

    @Transactional(readOnly = true)
    public List<Membresia> getMembresias() {
        return membresiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> getMembresia(Integer idMembresia) {
        return membresiaRepository.findById(idMembresia);
    }

    @Transactional(readOnly = true)
    public List<Membresia> getMembresiasPorCliente(String cedulaCliente) {
        return membresiaRepository.findByUsuarioCedulaOrderByFechaInicioDesc(cedulaCliente);
    }

    @Transactional(readOnly = true)
    public Optional<Membresia> getMembresiaVigente(String cedulaCliente) {
        return membresiaRepository.findMembresiaVigenteCliente(cedulaCliente, LocalDate.now());
    }

    @Transactional(readOnly = true)
    public boolean tieneMembresiaVigente(String cedulaCliente) {
        return membresiaRepository.tieneMembresiaVigente(cedulaCliente, LocalDate.now());
    }

    /**
     * Asigna una membresía calculando automáticamente la fecha de vencimiento
     * a partir de duracionDias del plan seleccionado.
     */
    @Transactional
    public Membresia asignarMembresia(String cedulaCliente, Integer idPlan, LocalDate fechaInicio) {
        Usuario cliente = usuarioRepository.findById(cedulaCliente)
                .orElseThrow(() -> new IllegalArgumentException("El cliente con cédula " + cedulaCliente + " no existe."));

        PlanMembresia plan = planMembresiaRepository.findById(idPlan)
                .orElseThrow(() -> new IllegalArgumentException("El plan seleccionado no existe."));

        if (!plan.isActivo()) {
            throw new IllegalStateException("El plan seleccionado no está disponible.");
        }

        if (fechaInicio == null) {
            fechaInicio = LocalDate.now();
        }

        // Cálculo de fecha de vencimiento según la duración del plan
        LocalDate fechaVencimiento = fechaInicio.plusDays(plan.getDuracionDias());

        Membresia membresia = new Membresia();
        membresia.setUsuario(cliente);
        membresia.setPlanMembresia(plan);
        membresia.setFechaInicio(fechaInicio);
        membresia.setFechaVencimiento(fechaVencimiento);
        membresia.setEstado("Activa");

        return membresiaRepository.save(membresia);
    }

    @Transactional
    public Membresia save(Membresia membresia) {
        if (membresia.getFechaVencimiento() == null && membresia.getPlanMembresia() != null && membresia.getFechaInicio() != null) {
            Integer dias = membresia.getPlanMembresia().getDuracionDias();
            if (dias != null) {
                membresia.setFechaVencimiento(membresia.getFechaInicio().plusDays(dias));
            }
        }
        return membresiaRepository.save(membresia);
    }

    @Transactional
    public void cancelarMembresia(Integer idMembresia) {
        membresiaRepository.findById(idMembresia).ifPresent(m -> {
            m.setEstado("Cancelada");
            membresiaRepository.save(m);
        });
    }

    @Transactional
    public void delete(Integer idMembresia) {
        if (!membresiaRepository.existsById(idMembresia)) {
            throw new IllegalArgumentException("La membresía con id " + idMembresia + " no existe.");
        }
        membresiaRepository.deleteById(idMembresia);
    }
}

