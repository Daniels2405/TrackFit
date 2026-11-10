/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.controller;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.TrackFit.domain.Horario;
import com.TrackFit.service.ClienteService;
import com.TrackFit.service.ReservaService;
import java.util.List;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/reserva")
public class ReservaController {

    private final ReservaService reservaService;
    private final ClienteService clienteService;
    private final MessageSource messageSource;

    // HU11: actividades disponibles para el cliente + sus reservas
    @GetMapping("/disponibles")
    public String disponibles(@RequestParam(value = "cedula", required = false) String cedula,
                              Model model) {

        model.addAttribute("clientes", clienteService.getClientes(true));
        model.addAttribute("cedula", cedula);

        if (cedula != null && !cedula.isBlank()) {
            List<Horario> horarios = reservaService.getHorariosDisponibles(cedula);

            model.addAttribute("membresia", reservaService.getMembresiaVigente(cedula).orElse(null));
            model.addAttribute("horarios", horarios);
            model.addAttribute("cupos", reservaService.getCuposDisponibles(horarios));
            model.addAttribute("misReservas", reservaService.getReservasCliente(cedula));
        }
        return "reserva/disponibles";
    }

    // HU12: el cliente reserva un espacio
    @PostMapping("/reservar")
    public String reservar(@RequestParam("cedula") String cedula,
                           @RequestParam(value = "idHorario", required = false) Integer idHorario,
                           RedirectAttributes redirectAttributes) {
        try {
            reservaService.reservar(cedula, idHorario);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("reserva.ok", null, LocaleContextHolder.getLocale()));
        } catch (IllegalArgumentException e) {
            // El Service ya manda el texto listo
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("error.general", null, LocaleContextHolder.getLocale()));
        }

        redirectAttributes.addAttribute("cedula", cedula);
        return "redirect:/reserva/disponibles";
    }
}
