package com.TrackFit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.PlanBeneficio;

/**
 * Repositorio para la entidad PlanBeneficio (HU04 - Beneficios de planes).
 * 
 * @author danie
 */
@Repository
public interface PlanBeneficioRepository extends JpaRepository<PlanBeneficio, Integer> {

    // Recupera los beneficios de un plan específico
    List<PlanBeneficio> findByPlanMembresiaIdPlan(Integer idPlan);

    // Elimina todos los beneficios asociados a un plan
    void deleteByPlanMembresiaIdPlan(Integer idPlan);
}

