package com.TrackFit.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.TrackFit.domain.Rol;
import com.TrackFit.domain.Usuario;
import com.TrackFit.service.RolService;
import com.TrackFit.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * Controlador web para la administración de Usuarios y Roles (HU01).
 * 
 * @author danie
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final RolService rolService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(Model model) {
        var usuarios = usuarioService.getUsuarios(false);
        var roles = rolService.getRoles();
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        model.addAttribute("rolesDisponibles", roles);
        model.addAttribute("usuario", new Usuario());
        return "usuario/listado";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid Usuario usuario,
                          BindingResult bindingResult,
                          @RequestParam(value = "rolesSeleccionados", required = false) List<Integer> rolesIds,
                          @RequestParam(value = "modoEdicion", defaultValue = "false") boolean modoEdicion,
                          RedirectAttributes redirectAttributes) {
        String tipo = "error";
        String mensaje = "form.datos.invalidos";

        // Validación de cédula única en creación
        if (!modoEdicion && usuarioService.existeCedula(usuario.getCedula())) {
            bindingResult.rejectValue("cedula", "error.cedula", "usuario.error.cedula.existe");
            mensaje = "usuario.error.cedula.existe";
        }

        // Validación de username único
        if (usuarioService.existeUsername(usuario.getUsername(), modoEdicion ? usuario.getCedula() : null)) {
            bindingResult.rejectValue("username", "error.username", "usuario.error.username.existe");
            mensaje = "usuario.error.username.existe";
        }

        // Validación de correo único
        if (usuarioService.existeCorreo(usuario.getCorreo(), modoEdicion ? usuario.getCedula() : null)) {
            bindingResult.rejectValue("correo", "error.correo", "usuario.error.correo.existe");
            mensaje = "usuario.error.correo.existe";
        }

        // Validación de rol obligatorio
        if (rolesIds == null || rolesIds.isEmpty()) {
            mensaje = "usuario.error.rol.obligatorio";
        }

        if (bindingResult.hasErrors()) {
            tipo = "error";
        } else {
            try {
                if (rolesIds != null && !rolesIds.isEmpty()) {
                    List<Rol> listaRoles = new ArrayList<>();
                    for (Integer idRol : rolesIds) {
                        rolService.getRol(idRol).ifPresent(listaRoles::add);
                    }
                    usuario.setRoles(listaRoles);
                }

                usuarioService.save(usuario);
                tipo = "todoOk";
                mensaje = modoEdicion ? "accion.actualizado.ok" : "accion.guardado.ok";
            } catch (Exception e) {
                tipo = "error";
                mensaje = "error.general";
            }
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @PostMapping("/eliminar")
    public String eliminar(@RequestParam String cedula, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.eliminado.ok";

        try {
            usuarioService.delete(cedula);
        } catch (DataIntegrityViolationException e) {
            tipo = "error";
            mensaje = "error.general";
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }

    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam String cedula, RedirectAttributes redirectAttributes) {
        String tipo = "todoOk";
        String mensaje = "accion.actualizado.ok";

        try {
            usuarioService.cambiarEstado(cedula);
        } catch (Exception e) {
            tipo = "error";
            mensaje = "error.general";
        }

        redirectAttributes.addFlashAttribute(tipo, messageSource.getMessage(mensaje, null, Locale.getDefault()));
        return "redirect:/usuario/listado";
    }
}

