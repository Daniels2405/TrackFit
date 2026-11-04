package com.TrackFit.service;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.PlanBeneficio;
import com.TrackFit.repository.PlanBeneficioRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Servicio para la gestión de beneficios de planes de membresía (HU04).
 * 
 * @author danie
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PlanBeneficioService {

    private final PlanBeneficioRepository planBeneficioRepository;

    @Transactional(readOnly = true)
    public List<PlanBeneficio> getBeneficiosPorPlan(Integer idPlan) {
        return planBeneficioRepository.findByPlanMembresiaIdPlan(idPlan);
    }

    @Transactional(readOnly = true)
    public Optional<PlanBeneficio> getBeneficio(Integer idBeneficio) {
        return planBeneficioRepository.findById(idBeneficio);
    }

    @Transactional
    public PlanBeneficio save(PlanBeneficio planBeneficio) {
        return planBeneficioRepository.save(planBeneficio);
    }

    @Transactional
    public void delete(Integer idBeneficio) {
        if (!planBeneficioRepository.existsById(idBeneficio)) {
            throw new IllegalArgumentException("El beneficio con id " + idBeneficio + " no existe.");
        }
        try {
            planBeneficioRepository.deleteById(idBeneficio);
            planBeneficioRepository.flush();
        } catch (DataIntegrityViolationException e) {
            log.warn("No fue posible eliminar el beneficio {}", idBeneficio, e);
            throw e;
        }
    }
}