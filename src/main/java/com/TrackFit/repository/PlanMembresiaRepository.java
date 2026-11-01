package com.TrackFit.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.PlanMembresia;

/**
 * Repositorio para la entidad PlanMembresia.
 * 
 * @author danie
 */
@Repository
public interface PlanMembresiaRepository extends JpaRepository<PlanMembresia, Integer> {

    // Recupera solo los planes activos (para asignación de membresías y vista cliente)
    List<PlanMembresia> findByActivoTrue();

    // Consulta para verificar existencia por nombre (evitar duplicados según UNIQUE)
    boolean existsByNombre(String nombre);

    // Consulta para verificar duplicado de nombre excluyendo el plan actual (en edición)
    boolean existsByNombreAndIdPlanNot(String nombre, Integer idPlan);

    // Busca un plan por su nombre exacto
    Optional<PlanMembresia> findByNombre(String nombre);
}