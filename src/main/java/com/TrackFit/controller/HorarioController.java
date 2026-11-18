/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.controller;

import com.TrackFit.domain.Horario;
import com.TrackFit.service.HorarioService;
import com.TrackFit.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//Controlador encargado de gestionar los horarios del gimnasio
@Controller
@RequiredArgsConstructor
@RequestMapping("/horario")
public class HorarioController {

    //Servicio que permite gestionar los horarios
    private final HorarioService horarioService;

    //Servicio que permite consultar las actividades disponibles
    private final ActividadService actividadService;

    //Permite mostrar los mensajes en el idioma elegido
    private final MessageSource messageSource;

    //Método que muestra el listado de horarios y el formulario
    @GetMapping("/listado")
    public String listado(Model model) {

        //Obtiene todos los horarios registrados
        java.util.List<Horario> horarios = horarioService.getHorarios();

        //Envía los horarios a la página HTML
        model.addAttribute("horarios", horarios);

        //Guarda los espacios disponibles de cada horario para mostrarlos en la página web
        java.util.Map<Integer, Integer> espaciosDisponibles
                = new java.util.HashMap<>();

        //Recorre los horarios registrados
        for (Horario horario : horarios) {
            //Calcula los espacios disponibles de cada horario
            int disponibles = horarioService.getEspaciosDisponibles(horario);

            //Guarda el resultado utilizando el ID del horario
            espaciosDisponibles.put(horario.getIdHorario(), disponibles);
        }

        //Envía los espacios disponibles a la página HTML
        model.addAttribute("espaciosDisponibles", espaciosDisponibles);

        //Obtiene las actividades para mostrarlas en el formulario
        model.addAttribute("actividades", actividadService.getActividades());

        //Crea un objeto vacío para registrar un nuevo horario
        model.addAttribute("horario", new Horario());

        //Retorna la página HTML de horarios
        return "horario/listado";

    }

    //Método que recibe los datos del formulario y guarda el horario
    @PostMapping("/guardar")
    public String guardar(Horario horario,
            RedirectAttributes redirectAttributes) {

        try {

            //Guarda el horario utilizando el servicio
            horarioService.save(horario);

            //Muestra un mensaje cuando se registra correctamente
            redirectAttributes.addFlashAttribute(
                    "todoOk", mensaje("horario.guardado.ok"));

        } catch (IllegalArgumentException e) {

            //Muestra el mensaje de las validaciones del servicio
            redirectAttributes.addFlashAttribute(
                    "error", mensaje(e.getMessage() != null ? e.getMessage() : "error.general"));

        } catch (Exception e) {

            //Muestra un mensaje si ocurre otro error
            redirectAttributes.addFlashAttribute(
                    "error", mensaje("horario.error.registrar"));
        }

        //Regresa al listado de horarios
        return "redirect:/horario/listado";
    }

    // Traduce una clave de messages.properties al idioma que eligió el usuario.
    // Si la clave no existe, devuelve el texto tal cual.
    private String mensaje(String clave, Object... args) {
        return messageSource.getMessage(clave, args, clave, LocaleContextHolder.getLocale());
    }
}
