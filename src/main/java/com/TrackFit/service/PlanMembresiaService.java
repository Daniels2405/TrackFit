package com.TrackFit.service;

import com.TrackFit.domain.PlanBeneficio;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.PlanMembresia;
import com.TrackFit.repository.PlanMembresiaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión integral de planes de membresía (HU04).
 * 
 * @author danie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanMembresiaService {

    private final PlanMembresiaRepository planMembresiaRepository;
    private final PlanBeneficioService planBeneficioService;

    @Transactional(readOnly = true)
    public List<PlanMembresia> getPlanes(boolean activo) {
        if (activo) {
            return planMembresiaRepository.findByActivoTrue();
        } else {
            return planMembresiaRepository.findAll();
        }
    }

    @Transactional(readOnly = true)
    public Optional<PlanMembresia> getPlan(Integer idPlan) {
        return planMembresiaRepository.findById(idPlan);
    }

    @Transactional(readOnly = true)
    public boolean existeNombre(String nombre) {
        return planMembresiaRepository.existsByNombre(nombre);
    }

    @Transactional(readOnly = true)
    public boolean existeNombreEnOtroPlan(String nombre, Integer idPlan) {
        if (idPlan == null) {
            return existeNombre(nombre);
        }
        return planMembresiaRepository.existsByNombreAndIdPlanNot(nombre, idPlan);
    }

    @Transactional
    public PlanMembresia save(PlanMembresia planMembresia) {
        return planMembresiaRepository.save(planMembresia);
    }

    /**
     * Guarda el plan y sincroniza su lista de beneficios proporcionada como texto multilínea.
     */
    @Transactional
    public PlanMembresia saveConBeneficios(PlanMembresia planFormulario, String textoBeneficios) {
        
        // 1. Obtener el plan de la base de datos o crear uno nuevo
        PlanMembresia planPersistido = planFormulario.getIdPlan() != null 
            ? planMembresiaRepository.findById(planFormulario.getIdPlan()).orElse(new PlanMembresia()) 
            : new PlanMembresia();

        // 2. Actualizar datos nativos del plan
        planPersistido.setNombre(planFormulario.getNombre());
        planPersistido.setPrecio(planFormulario.getPrecio());
        planPersistido.setDuracionDias(planFormulario.getDuracionDias());
        planPersistido.setAnticipacionReservaDias(planFormulario.getAnticipacionReservaDias());
        planPersistido.setRutaImagen(planFormulario.getRutaImagen());
        planPersistido.setActivo(planFormulario.isActivo());

        // 3. Sincronizar beneficios inteligentemente en vez de borrar/crear
        if (textoBeneficios == null || textoBeneficios.trim().isEmpty()) {
            planPersistido.clearBeneficios();
        } else {
            // Limpiamos y preparamos las nuevas líneas ingresadas en el textarea
            List<String> nuevasDescripciones = new java.util.ArrayList<>();
            for (String linea : textoBeneficios.split("\\r?\\n")) {
                if (!linea.trim().isEmpty()) nuevasDescripciones.add(linea.trim());
            }

            // Eliminar beneficios que ya no están en el texto
            planPersistido.getBeneficios().removeIf(b -> !nuevasDescripciones.contains(b.getDescripcion()));

            // Obtener los que ya existen para no duplicarlos
            List<String> descripcionesExistentes = planPersistido.getBeneficios().stream()
                .map(PlanBeneficio::getDescripcion)
                .toList();

            // Agregar solo los que son completamente nuevos
            for (String desc : nuevasDescripciones) {
                if (!descripcionesExistentes.contains(desc)) {
                    PlanBeneficio nuevoBeneficio = new PlanBeneficio();
                    nuevoBeneficio.setDescripcion(desc);
                    planPersistido.addBeneficio(nuevoBeneficio);
                }
            }
        }

        // 4. Guardar
        return planMembresiaRepository.save(planPersistido);
    }

    @Transactional
    public void delete(Integer idPlan) {
        if (!planMembresiaRepository.existsById(idPlan)) {
            throw new IllegalArgumentException("El plan con id " + idPlan + " no existe.");
        }
        try {
            // Ya no necesitas borrar los beneficios manualmente, Hibernate lo hace por ti
            planMembresiaRepository.deleteById(idPlan);
            planMembresiaRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("No fue posible eliminar el plan {} porque tiene membresías asociadas", idPlan, e);
            throw e;
        }
    }

    @Transactional
    public void cambiarEstado(Integer idPlan) {
        planMembresiaRepository.findById(idPlan).ifPresent(plan -> {
            plan.setActivo(!plan.isActivo());
            planMembresiaRepository.save(plan);
        });
    }
}
