/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.controller;

import com.TrackFit.domain.Pago;
import com.TrackFit.service.PagoService;
import com.TrackFit.service.MembresiaService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.TrackFit.domain.Usuario;
import com.TrackFit.service.UsuarioService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pago")
public class PagoController {

    private final PagoService pagoService;
    private final MembresiaService membresiaService;
    private final UsuarioService usuarioService;

    @GetMapping("/listado")
    public String listado(Model model) {

        model.addAttribute("pagos", pagoService.getPagos());
        model.addAttribute("membresias", membresiaService.getMembresias());
        model.addAttribute("nuevoPago", new Pago());

        return "pago/listado";
    }

    @PostMapping("/guardar")
    public String guardar(Pago pago, RedirectAttributes redirectAttributes) {

        try {
            if (pago.getMembresia() == null
                    || pago.getMembresia().getIdMembresia() == null) {
                throw new IllegalArgumentException("Seleccione una membresía");
            }

            var membresiaEncontrada = membresiaService.getMembresias()
                    .stream()
                    .filter(m -> m.getIdMembresia().equals(
                            pago.getMembresia().getIdMembresia()))
                    .findFirst();

            if (membresiaEncontrada.isEmpty()) {
                throw new IllegalArgumentException("La membresía no existe");
            }

            pago.setMembresia(membresiaEncontrada.get());

            if (pago.getMonto() == null
                    || pago.getMonto().signum() <= 0) {
                throw new IllegalArgumentException("El monto debe ser mayor a cero");
            }

            if (pago.getMetodoPago() == null
                    || (!pago.getMetodoPago().equals("Efectivo")
                    && !pago.getMetodoPago().equals("Tarjeta")
                    && !pago.getMetodoPago().equals("SINPE")
                    && !pago.getMetodoPago().equals("Transferencia"))) {

                throw new IllegalArgumentException("Método de pago no válido");
            }

            if (pago.getFecha() == null) {
                pago.setFecha(LocalDate.now());
            }

            Usuario recepcion = usuarioService.getUsuario("100000002")
                    .orElseThrow(()
                            -> new IllegalArgumentException("No existe el usuario de recepción"));

            pago.setUsuarioRegistra(recepcion);

            pagoService.save(pago);

            redirectAttributes.addFlashAttribute(
                    "todoOk", "Pago registrado correctamente");

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error", "No se pudo registrar el pago: " + e.getMessage());
        }

        return "redirect:/pago/listado";
    }
}
