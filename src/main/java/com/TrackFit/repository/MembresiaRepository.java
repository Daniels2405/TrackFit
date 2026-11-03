package com.TrackFit.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.TrackFit.domain.Membresia;

/**
 * Repositorio para la entidad Membresia (HU05 - Asignación de membresías y HU06 - Consulta).
 * 
 * @author danie
 */
@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Integer> {

    // Membresías de un cliente ordenadas de la más reciente a la más antigua
    List<Membresia> findByUsuarioCedulaOrderByFechaInicioDesc(String cedulaCliente);

    // Membresías asociadas a un plan
    List<Membresia> findByPlanMembresiaIdPlan(Integer idPlan);

    // Consulta para obtener la membresía actualmente vigente de un cliente (Activa y fechaVencimiento >= hoy)
    @Query("SELECT m FROM Membresia m WHERE m.usuario.cedula = :cedula AND m.estado = 'Activa' AND m.fechaVencimiento >= :hoy")
    Optional<Membresia> findMembresiaVigenteCliente(@Param("cedula") String cedula, @Param("hoy") LocalDate hoy);

    // Comprobar si el cliente ya tiene una membresía vigente activa
    @Query("SELECT COUNT(m) > 0 FROM Membresia m WHERE m.usuario.cedula = :cedula AND m.estado = 'Activa' AND m.fechaVencimiento >= :hoy")
    boolean tieneMembresiaVigente(@Param("cedula") String cedula, @Param("hoy") LocalDate hoy);
}

