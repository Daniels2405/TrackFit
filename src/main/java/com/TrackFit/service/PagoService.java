/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.service;

import com.TrackFit.domain.Pago;
import com.TrackFit.repository.PagoRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;

    @Transactional(readOnly = true)
    public List<Pago> getPagos() {
        return pagoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Pago> getPagosPorCliente(String cedula) {
        return pagoRepository.findByMembresiaUsuarioCedula(cedula);
    }

    @Transactional
    public void save(Pago pago) {
        pagoRepository.save(pago);
    }
}
