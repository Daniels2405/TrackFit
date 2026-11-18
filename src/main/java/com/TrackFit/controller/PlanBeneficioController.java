package com.TrackFit.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.PlanBeneficio;
import com.TrackFit.service.PlanBeneficioService;
import com.TrackFit.service.PlanMembresiaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la gestión individual de Beneficios de Planes (HU04).
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/plan-beneficio")
public class PlanBeneficioController {

    private final PlanBeneficioService planBeneficioService;
    private final PlanMembresiaService planMembresiaService;
    private final MessageSource messageSource;

    @PostMapping("/guardar")
    public String guardar(@Valid PlanBeneficio planBeneficio,
                          BindingResult bindingResult,
                          @RequestParam("idPlan") Integer idPlan,
                          RedirectAttributes redirectAttributes) {
        String tipo = "error";
        String mensaje = "form.datos.invalidos";

        if (bindingResult.hasErrors()) {
            tipo = "error";
        } else {
            try {
                var planOpt = planMembresiaService.getPlan(idPlan);
                if (planOpt.isPresent()) {
                    planBeneficio.setPlanMembresia(planOpt.get());
                    planBeneficioService.save(planBeneficio);
                    tipo = "todoOk";
                    mensaje = "accion.guardado.ok";
                } else {
                    mensaje = "plan.error.no.existe";
                }
            } catch (Exception e) {
                tipo = "error";
                mensaje = "error.general";
            }
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/plan/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam Integer idBeneficio, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.eliminado.ok";

        try {
            planBeneficioService.delete(idBeneficio);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/plan/listado";
    }
}
