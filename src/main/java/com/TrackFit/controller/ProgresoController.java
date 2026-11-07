package com.TrackFit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TrackFit.service.ProgresoService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/progreso")
public class ProgresoController {

    private final ProgresoService progresoService;

    @GetMapping("/listado")
    public String listado(
            @RequestParam(value = "cedula", required = false) String cedula,
            Model model) {

        model.addAttribute(
                "progresos",
                progresoService.getProgresos());

        return "progreso/listado";
    }
}
