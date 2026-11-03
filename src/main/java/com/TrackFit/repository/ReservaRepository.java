/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.TrackFit.repository;

import com.TrackFit.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import com.TrackFit.domain.Horario;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

//Repositorio encargado de acceder a la tabla reserva
public interface ReservaRepository extends JpaRepository<Reserva, Integer> {

    //Cuenta las reservas de un horario según su estado
    long countByHorarioIdHorarioAndEstado(
            Integer idHorario,
            String estado);
    
    @Query("SELECT h FROM Horario h WHERE h.fecha BETWEEN :desde AND :hasta "
         + "AND h.actividad.activo = true ORDER BY h.fecha, h.horaInicio")
    List<Horario> buscarHorarios(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query(value = "SELECT id_actividad FROM actividad_plan WHERE id_plan = :idPlan", nativeQuery = true)
    List<Integer> actividadesPermitidas(@Param("idPlan") Integer idPlan);

    Optional<Reserva> findByClienteCedulaAndHorarioIdHorario(String cedula, Integer idHorario);

    List<Reserva> findByClienteCedulaOrderByHorarioFechaAscHorarioHoraInicioAsc(String cedula);
}
