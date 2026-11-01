package com.TrackFit.controller;

import java.time.LocalDate;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.Membresia;
import com.TrackFit.service.ClienteService;
import com.TrackFit.service.MembresiaService;
import com.TrackFit.service.PlanMembresiaService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la asignación y gestión de Membresías (HU05).
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/membresia")
public class MembresiaController {

    private final MembresiaService membresiaService;
    private final ClienteService clienteService;
    private final PlanMembresiaService planMembresiaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var membresias = membresiaService.getMembresias();
        var clientes = clienteService.getClientes(true);
        var planes = planMembresiaService.getPlanes(true);

        model.addAttribute("membresias", membresias);
        model.addAttribute("totalMembresias", membresias.size());
        model.addAttribute("clientesDisponibles", clientes);
        model.addAttribute("planesDisponibles", planes);
        model.addAttribute("nuevaMembresia", new Membresia());
        return "membresia/listado";
    }

    @PostMapping("/asignar")
    public String asignar(@RequestParam("cedulaCliente") String cedulaCliente,
                          @RequestParam("idPlan") Integer idPlan,
                          @RequestParam(value = "fechaInicio", required = false) LocalDate fechaInicio,
                          RedirectAttributes redirectAttributes) {
        String tipo = "error";
        String mensaje = "form.datos.invalidos";

        if (cedulaCliente == null || cedulaCliente.isBlank()) {
            mensaje = "membresia.error.cliente.obligatorio";
        } else if (idPlan == null) {
            mensaje = "membresia.error.plan.obligatorio";
        } else {
            try {
                membresiaService.asignarMembresia(cedulaCliente, idPlan, fechaInicio);
                tipo = "todoOk";
                mensaje = "accion.guardado.ok";
            } catch (IllegalStateException e) {
                tipo = "error";
                mensaje = "membresia.error.plan.inactivo";
            } catch (Exception e) {
                tipo = "error";
                mensaje = "error.general";
            }
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/membresia/listado";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam("idMembresia") Integer idMembresia, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.actualizado.ok";

        try {
            membresiaService.cancelarMembresia(idMembresia);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/membresia/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam("idMembresia") Integer idMembresia, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.eliminado.ok";

        try {
            membresiaService.delete(idMembresia);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/membresia/listado";
    }
}

