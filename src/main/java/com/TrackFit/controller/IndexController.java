package com.TrackFit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.TrackFit.service.ClienteService;
import com.TrackFit.service.MembresiaService;
import com.TrackFit.service.PlanMembresiaService;
import com.TrackFit.service.UsuarioService;

import lombok.RequiredArgsConstructor;

/**
 * Controlador principal para la página de inicio y panel de control (Dashboard).
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
public class IndexController {

    private final PlanMembresiaService planMembresiaService;
    private final ClienteService clienteService;
    private final MembresiaService membresiaService;
    private final UsuarioService usuarioService;

    @GetMapping("/")
    public String index(Model model) {
        var planes = planMembresiaService.getPlanes(true);
        var clientes = clienteService.getClientes(false);
        var membresias = membresiaService.getMembresias();
        var usuarios = usuarioService.getUsuarios(false);

        model.addAttribute("planesActivos", planes);
        model.addAttribute("totalPlanes", planes.size());
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("totalMembresias", membresias.size());
        model.addAttribute("totalUsuarios", usuarios.size());

        return "index";
    }
}

