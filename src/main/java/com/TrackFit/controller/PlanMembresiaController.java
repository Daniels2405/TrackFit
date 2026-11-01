package com.TrackFit.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.PlanMembresia;
import com.TrackFit.service.PlanMembresiaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la gestión de Planes de Membresía (HU04).
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/plan")
public class PlanMembresiaController {

    private final PlanMembresiaService planMembresiaService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var planes = planMembresiaService.getPlanes(false);
        model.addAttribute("planes", planes);
        model.addAttribute("totalPlanes", planes.size());
        model.addAttribute("plan", new PlanMembresia());
        return "plan/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid PlanMembresia planMembresia,
                          BindingResult bindingResult,
                          @RequestParam(value = "beneficiosTexto", required = false) String beneficiosTexto,
                          RedirectAttributes redirectAttributes) {
        String tipo = "error";
        String mensaje = "form.datos.invalidos";

        // Validación de unicidad de nombre de plan
        if (planMembresiaService.existeNombreEnOtroPlan(planMembresia.getNombre(), planMembresia.getIdPlan())) {
            bindingResult.rejectValue("nombre", "error.nombre", "plan.error.nombre.existe");
            mensaje = "plan.error.nombre.existe";
        }

        if (bindingResult.hasErrors()) {
            tipo = "error";
        } else {
            try {
                boolean esNuevo = planMembresia.getIdPlan() == null;
                planMembresiaService.saveConBeneficios(planMembresia, beneficiosTexto);
                tipo = "todoOk";
                mensaje = esNuevo ? "accion.guardado.ok" : "accion.actualizado.ok";
            } catch (Exception e) {
                tipo = "error";
                mensaje = "error.general";
            }
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/plan/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idPlan, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.eliminado.ok";

        try {
            planMembresiaService.delete(idPlan);
        } catch (DataIntegrityViolationException e) {
            tipo = "error";
            mensaje = "plan.error.enuso";
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/plan/listado";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Integer idPlan, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.actualizado.ok";

        try {
            planMembresiaService.cambiarEstado(idPlan);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/plan/listado";
    }
}
