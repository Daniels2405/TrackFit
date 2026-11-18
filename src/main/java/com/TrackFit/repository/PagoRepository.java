/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.TrackFit.repository;

import com.TrackFit.domain.Pago;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PagoRepository extends JpaRepository<Pago, Integer> {

    List<Pago> findByMembresiaUsuarioCedula(String cedula);

    // ¿La membresía tiene pagos registrados? (si los tiene, no se puede eliminar)
    boolean existsByMembresiaIdMembresia(Integer idMembresia);

    // Ids de las membresías que tienen al menos un pago
    @Query("SELECT DISTINCT p.membresia.idMembresia FROM Pago p")
    List<Integer> findIdsMembresiasConPagos();

}
