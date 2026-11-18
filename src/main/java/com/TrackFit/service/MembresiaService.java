package com.TrackFit.service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Membresia;
import com.TrackFit.domain.PlanMembresia;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.MembresiaRepository;
import com.TrackFit.repository.PagoRepository;
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
    private final PagoRepository pagoRepository;

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
        return membresiaRepository.findVigentesCliente(cedulaCliente, LocalDate.now())
                .stream()
                .findFirst();
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

        // No se permite que el cliente tenga dos membresías activas en las mismas fechas.
        // Para renovar, la nueva debe empezar el día después de que vence la actual.
        List<Membresia> traslapes = membresiaRepository.findTraslapes(cedulaCliente, fechaInicio, fechaVencimiento);
        if (!traslapes.isEmpty()) {
            throw new MembresiaTraslapadaException(traslapes.get(0).getFechaVencimiento().plusDays(1));
        }

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
        // Una membresía con pagos no se borra, para no perder el historial de ingresos
        if (tienePagos(idMembresia)) {
            throw new IllegalStateException("membresia.error.tiene.pagos");
        }
        membresiaRepository.deleteById(idMembresia);
    }

    @Transactional(readOnly = true)
    public boolean tienePagos(Integer idMembresia) {
        return pagoRepository.existsByMembresiaIdMembresia(idMembresia);
    }

    // Ids de las membresías con pagos (en la vista se desactiva el botón de eliminar)
    @Transactional(readOnly = true)
    public Set<Integer> getIdsConPagos() {
        return new HashSet<>(pagoRepository.findIdsMembresiasConPagos());
    }

    /**
     * Se lanza cuando la nueva membresía se traslapa con otra activa del mismo cliente.
     * Indica desde qué fecha se puede asignar la nueva (el día siguiente al vencimiento).
     */
    public static class MembresiaTraslapadaException extends RuntimeException {

        private final LocalDate disponibleDesde;

        public MembresiaTraslapadaException(LocalDate disponibleDesde) {
            super("membresia.error.traslape");
            this.disponibleDesde = disponibleDesde;
        }

        public LocalDate getDisponibleDesde() {
            return disponibleDesde;
        }
    }
}

