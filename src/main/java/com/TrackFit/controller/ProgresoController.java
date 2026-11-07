package com.TrackFit.controller;

import java.time.LocalDate;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.Progreso;
import com.TrackFit.service.ProgresoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/progreso")
public class ProgresoController {

    private final ProgresoService progresoService;

    // HU18: historial de progreso (opcionalmente filtrado por cédula del cliente)
    @GetMapping("/listado")
    public String listado(
            @RequestParam(value = "cedula", required = false) String cedula,
            Model model) {

        if (cedula != null && !cedula.isBlank()) {
            model.addAttribute("progresos", progresoService.getProgresosPorCliente(cedula));
        } else {
            model.addAttribute("progresos", progresoService.getProgresos());
        }

        // Para el filtro por cliente de la vista
        model.addAttribute("cedula", cedula);
        model.addAttribute("clientes", progresoService.getClientesActivos());

        return "progreso/listado";
    }

    // HU17: muestra el formulario para registrar una medición
    @GetMapping("/nuevo")
    public String nuevo(Model model) {

        Progreso progreso = new Progreso();
        progreso.setFecha(LocalDate.now());   // fecha de hoy por defecto

        return mostrarFormulario(progreso, model);
    }

    // HU17: guarda la medición y vuelve al historial.
    // Si algo falla, se vuelve a mostrar el formulario con los datos que
    // el usuario ya había escrito (no se pierden).
    @PostMapping("/guardar")
    public String guardar(Progreso progreso,
                          BindingResult bindingResult,
                          Model model,
                          RedirectAttributes redirectAttributes) {

        // Datos con formato inválido (ej. texto en el peso o una fecha mal formada)
        if (bindingResult.hasErrors()) {
            model.addAttribute("error",
                    "No se pudo registrar el progreso: revise el formato de los datos");
            return mostrarFormulario(progreso, model);
        }

        try {
            progresoService.registrar(progreso);

        } catch (Exception e) {

            model.addAttribute("error",
                    "No se pudo registrar el progreso: " + e.getMessage());
            return mostrarFormulario(progreso, model);
        }

        redirectAttributes.addFlashAttribute(
                "todoOk", "Progreso registrado correctamente");

        return "redirect:/progreso/listado";
    }

    // Carga lo que necesita la vista del formulario
    private String mostrarFormulario(Progreso progreso, Model model) {

        model.addAttribute("progreso", progreso);
        model.addAttribute("clientes", progresoService.getClientesActivos());
        model.addAttribute("entrenadores", progresoService.getEntrenadoresActivos());

        return "progreso/formulario";
    }
}
