package com.TrackFit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Progreso;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.ClienteRepository;
import com.TrackFit.repository.EntrenadorRepository;
import com.TrackFit.repository.ProgresoRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProgresoService {

    private final ProgresoRepository progresoRepository;
    private final ClienteRepository clienteRepository;
    private final EntrenadorRepository entrenadorRepository;

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

    // Listas para llenar los <select> del formulario (HU17)
    @Transactional(readOnly = true)
    public List<Usuario> getClientesActivos() {
        return clienteRepository.findClientesActivos();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getEntrenadoresActivos() {
        return entrenadorRepository.findEntrenadoresActivos();
    }

    /**
     * HU17: registra una medición de progreso aplicando las reglas de negocio.
     * Lanza IllegalArgumentException con la clave del mensaje (messages.properties)
     * si algo no cumple; el controller la traduce al idioma activo.
     */
    @Transactional
    public Progreso registrar(Progreso progreso) {

        // Cliente: debe venir seleccionado y tener el rol CLIENTE
        if (progreso.getCliente() == null
                || progreso.getCliente().getCedula() == null
                || progreso.getCliente().getCedula().isBlank()) {
            throw new IllegalArgumentException("progreso.error.cliente.obligatorio");
        }
        Usuario cliente = clienteRepository
                .findClienteByCedula(progreso.getCliente().getCedula())
                .orElseThrow(() -> new IllegalArgumentException("progreso.error.cliente.no.existe"));

        // Entrenador: debe venir seleccionado y tener el rol ENTRENADOR
        if (progreso.getEntrenador() == null
                || progreso.getEntrenador().getCedula() == null
                || progreso.getEntrenador().getCedula().isBlank()) {
            throw new IllegalArgumentException("progreso.error.entrenador.obligatorio");
        }
        Usuario entrenador = entrenadorRepository
                .findEntrenadorByCedula(progreso.getEntrenador().getCedula())
                .orElseThrow(() -> new IllegalArgumentException("progreso.error.entrenador.no.existe"));

        // Fecha: obligatoria y no puede ser futura
        if (progreso.getFecha() == null) {
            progreso.setFecha(LocalDate.now());
        }
        if (progreso.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("progreso.error.fecha.futura");
        }

        // Peso: mayor a cero (misma regla del CHECK de la tabla)
        if (progreso.getPeso() == null || progreso.getPeso().signum() <= 0) {
            throw new IllegalArgumentException("progreso.error.peso");
        }

        // % de grasa: opcional, pero entre 0 y 100
        BigDecimal grasa = progreso.getPorcentajeGrasa();
        if (grasa != null
                && (grasa.signum() < 0 || grasa.compareTo(BigDecimal.valueOf(100)) > 0)) {
            throw new IllegalArgumentException("progreso.error.grasa");
        }

        // Observaciones vacías se guardan como NULL
        if (progreso.getObservaciones() != null && progreso.getObservaciones().isBlank()) {
            progreso.setObservaciones(null);
        }

        progreso.setCliente(cliente);
        progreso.setEntrenador(entrenador);
        return progresoRepository.save(progreso);
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
