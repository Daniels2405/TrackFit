package com.TrackFit.service;

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
    public PlanMembresia saveConBeneficios(PlanMembresia planMembresia, String textoBeneficios) {
        PlanMembresia planGuardado = planMembresiaRepository.save(planMembresia);
        if (textoBeneficios != null) {
            planBeneficioService.sincronizarBeneficiosTexto(planGuardado, textoBeneficios);
        }
        return planGuardado;
    }

    @Transactional
    public void delete(Integer idPlan) {
        if (!planMembresiaRepository.existsById(idPlan)) {
            throw new IllegalArgumentException("El plan con id " + idPlan + " no existe.");
        }
        try {
            // Eliminar primero sus beneficios asociados
            planBeneficioService.deletePorPlan(idPlan);
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
