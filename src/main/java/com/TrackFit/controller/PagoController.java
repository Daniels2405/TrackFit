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
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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

    //Permite mostrar los mensajes en el idioma elegido
    private final MessageSource messageSource;

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
                throw new IllegalArgumentException("pago.error.membresia.obligatoria");
            }

            var membresiaEncontrada = membresiaService.getMembresias()
                    .stream()
                    .filter(m -> m.getIdMembresia().equals(
                            pago.getMembresia().getIdMembresia()))
                    .findFirst();

            if (membresiaEncontrada.isEmpty()) {
                throw new IllegalArgumentException("pago.error.membresia.no.existe");
            }

            pago.setMembresia(membresiaEncontrada.get());

            if (pago.getMonto() == null
                    || pago.getMonto().signum() <= 0) {
                throw new IllegalArgumentException("pago.error.monto.invalido");
            }

            if (pago.getMetodoPago() == null
                    || (!pago.getMetodoPago().equals("Efectivo")
                    && !pago.getMetodoPago().equals("Tarjeta")
                    && !pago.getMetodoPago().equals("SINPE")
                    && !pago.getMetodoPago().equals("Transferencia"))) {

                throw new IllegalArgumentException("pago.error.metodo.invalido");
            }

            if (pago.getFecha() == null) {
                pago.setFecha(LocalDate.now());
            }

            Usuario recepcion = usuarioService.getUsuario("100000002")
                    .orElseThrow(()
                            -> new IllegalArgumentException("pago.error.recepcion"));

            pago.setUsuarioRegistra(recepcion);

            pagoService.save(pago);

            redirectAttributes.addFlashAttribute(
                    "todoOk", mensaje("pago.guardado.ok"));

        } catch (IllegalArgumentException e) {

            // Validaciones: la excepción trae la clave del mensaje
            redirectAttributes.addFlashAttribute(
                    "error", mensaje("pago.error.registrar",
                            mensaje(e.getMessage() != null ? e.getMessage() : "error.general")));

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error", mensaje("pago.error.registrar", mensaje("error.general")));
        }

        return "redirect:/pago/listado";
    }

    // Traduce una clave de messages.properties al idioma que eligió el usuario.
    // Si la clave no existe, devuelve el texto tal cual.
    private String mensaje(String clave, Object... args) {
        return messageSource.getMessage(clave, args, clave, LocaleContextHolder.getLocale());
    }
}
