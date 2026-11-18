package com.TrackFit.controller;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.Usuario;
import com.TrackFit.service.ClienteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la gestión de Clientes / Socios desde el panel de Recepción (HU03).
 * Reutiliza la entidad Usuario restringiendo las operaciones exclusivamente al rol CLIENTE.
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/cliente")
public class ClienteController {

    private final ClienteService clienteService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@RequestParam(value = "filtro", required = false) String filtro, Model model) {
        var clientes = clienteService.buscarClientes(filtro);
        model.addAttribute("clientes", clientes);
        model.addAttribute("totalClientes", clientes.size());
        model.addAttribute("cliente", new Usuario());
        model.addAttribute("filtro", filtro);
        return "cliente/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario cliente,
                          BindingResult bindingResult,
                          @RequestParam(value = "modoEdicion", defaultValue = "false") boolean modoEdicion,
                          RedirectAttributes redirectAttributes) {
        String tipo = "error";
        String mensaje = "form.datos.invalidos";

        // Validación de cédula única en creación
        if (!modoEdicion && clienteService.existeCedula(cliente.getCedula())) {
            bindingResult.rejectValue("cedula", "error.cedula", "cliente.error.cedula.existe");
            mensaje = "cliente.error.cedula.existe";
        }

        // Validación de username único
        if (clienteService.existeUsername(cliente.getUsername(), modoEdicion ? cliente.getCedula() : null)) {
            bindingResult.rejectValue("username", "error.username", "cliente.error.username.existe");
            mensaje = "cliente.error.username.existe";
        }

        // Validación de correo único
        if (clienteService.existeCorreo(cliente.getCorreo(), modoEdicion ? cliente.getCedula() : null)) {
            bindingResult.rejectValue("correo", "error.correo", "cliente.error.correo.existe");
            mensaje = "cliente.error.correo.existe";
        }

        if (bindingResult.hasErrors()) {
            tipo = "error";
        } else {
            try {
                clienteService.saveCliente(cliente, modoEdicion);
                tipo = "todoOk";
                mensaje = modoEdicion ? "accion.actualizado.ok" : "accion.guardado.ok";
            } catch (IllegalStateException e) {
                tipo = "error";
                mensaje = "cliente.error.rol.no.configurado";
            } catch (Exception e) {
                tipo = "error";
                mensaje = "error.general";
            }
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/cliente/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam String cedula, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.eliminado.ok";

        try {
            clienteService.deleteCliente(cedula);
        } catch (DataIntegrityViolationException e) {
            tipo = "error";
            mensaje = "error.general";
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/cliente/listado";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam String cedula, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.actualizado.ok";

        try {
            clienteService.cambiarEstado(cedula);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, LocaleContextHolder.getLocale()));
        return "redirect:/cliente/listado";
    }
}

