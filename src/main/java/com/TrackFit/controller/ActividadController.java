/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.controller;

import com.TrackFit.domain.Actividad;
import com.TrackFit.service.ActividadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

//Controlador encargado de gestionar las actividades del gimnasio
@Controller
@RequiredArgsConstructor
@RequestMapping("/actividad")
public class ActividadController {

    //Servicio que contiene los métodos para gestionar actividades
    private final ActividadService actividadService;

    //Método que muestra el listado de actividades y el formulario
    @GetMapping("/listado")
    public String listado(Model model) {

        // Obtiene todas las actividades registradas
        model.addAttribute("actividades", actividadService.getActividades());

        // Crea un objeto vacío para registrar una nueva actividad
        model.addAttribute("actividad", new Actividad());

        // Retorna la página HTML de actividades
        return "actividad/listado";
    }

    //Método que recibe y guarda los datos del formulario
    @PostMapping("/guardar")
    public String guardar(Actividad actividad,
            RedirectAttributes redirectAttributes) {

        try {

            //Verifica que el nombre no esté vacío
            if (actividad.getNombre() == null
                    || actividad.getNombre().isBlank()) {
                throw new IllegalArgumentException(
                        "Debe ingresar el nombre de la actividad");
            }

            //Elimina espacios al inicio y al final del nombre
            actividad.setNombre(actividad.getNombre().trim());

            //Establece la actividad como activa
            actividad.setActivo(true);

            //Guarda la actividad en la base de datos
            actividadService.save(actividad);

            //Muestra un mensaje cuando se guarda correctamente
            redirectAttributes.addFlashAttribute(
                    "todoOk", "Actividad registrada correctamente");

        } catch (IllegalArgumentException e) {

            //Muestra el mensaje cuando la actividad ya existe o cuando el nombre ingresado no es válido
            redirectAttributes.addFlashAttribute(
                    "error", e.getMessage());

        } catch (Exception e) {

            //Muestra un mensaje si ocurre otro error
            redirectAttributes.addFlashAttribute(
                    "error", "No se pudo registrar la actividad");
        }

        //Regresa al listado de actividades
        return "redirect:/actividad/listado";
    }
}
