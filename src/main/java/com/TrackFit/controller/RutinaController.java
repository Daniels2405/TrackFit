package com.TrackFit.controller;
import java.math.BigDecimal;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.TrackFit.service.ClienteService;
import com.TrackFit.service.RutinaService;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rutina")
public class RutinaController {

    private final RutinaService rutinaService;
    private final ClienteService clienteService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(@RequestParam(value = "cedulaCliente", required = false) String cedulaCliente,
                          Model model) {

        model.addAttribute("clientes", clienteService.getClientes(true));
        model.addAttribute("entrenadores", rutinaService.getEntrenadores());
        model.addAttribute("ejercicios", rutinaService.getEjercicios());
        model.addAttribute("cedulaCliente", cedulaCliente);

        if (cedulaCliente != null && !cedulaCliente.isBlank()) {
            model.addAttribute("rutinaVigente", rutinaService.getRutinaVigente(cedulaCliente).orElse(null));
            model.addAttribute("rutinas", rutinaService.getRutinasPorCliente(cedulaCliente));
        }
        return "rutina/listado";
    }

    @GetMapping("/mi-rutina")
    public String miRutina(@RequestParam(value = "cedula", required = false) String cedula,
                           Model model) {

        model.addAttribute("clientes", clienteService.getClientes(true));
        model.addAttribute("cedula", cedula);

        if (cedula != null && !cedula.isBlank()) {
            model.addAttribute("rutina", rutinaService.getRutinaVigente(cedula).orElse(null));
        }
        return "rutina/mi-rutina";
    }
    @PostMapping("/crear")
    public String crear(@RequestParam("cedulaCliente") String cedulaCliente,
                        @RequestParam("cedulaEntrenador") String cedulaEntrenador,
                        @RequestParam("nombre") String nombre,
                        @RequestParam(value = "observaciones", required = false) String observaciones,
                        RedirectAttributes redirectAttributes) {
        try {
            rutinaService.crearRutina(cedulaCliente, cedulaEntrenador, nombre, observaciones);
            mostrarMensaje(redirectAttributes, "todoOk", "accion.guardado.ok");
        } catch (IllegalArgumentException e) {
            mostrarMensaje(redirectAttributes, "error", e.getMessage());
        } catch (Exception e) {
            mostrarMensaje(redirectAttributes, "error", "error.general");
        }

        redirectAttributes.addAttribute("cedulaCliente", cedulaCliente);
        return "redirect:/rutina/listado";
    }
    
    @PostMapping("/agregarEjercicio")
    public String agregarEjercicio(@RequestParam("cedulaCliente") String cedulaCliente,
                                   @RequestParam(value = "idRutina", required = false) Integer idRutina,
                                   @RequestParam(value = "idEjercicio", required = false) Integer idEjercicio,
                                   @RequestParam(value = "series", required = false) Integer series,
                                   @RequestParam(value = "repeticiones", required = false) Integer repeticiones,
                                   @RequestParam(value = "peso", required = false) BigDecimal peso,
                                   @RequestParam(value = "observaciones", required = false) String observaciones,
                                   RedirectAttributes redirectAttributes) {
        try {
            rutinaService.agregarEjercicio(idRutina, idEjercicio, series, repeticiones, peso, observaciones);
            mostrarMensaje(redirectAttributes, "todoOk", "rutina.ejercicio.agregado");
        } catch (IllegalArgumentException e) {
            mostrarMensaje(redirectAttributes, "error", e.getMessage());
        } catch (Exception e) {
            mostrarMensaje(redirectAttributes, "error", "error.general");
        }

        redirectAttributes.addAttribute("cedulaCliente", cedulaCliente);
        return "redirect:/rutina/listado";
    }

    @PostMapping("/quitarEjercicio")
    public String quitarEjercicio(@RequestParam("cedulaCliente") String cedulaCliente,
                                  @RequestParam("idRutina") Integer idRutina,
                                  @RequestParam("idRutinaEjercicio") Integer idRutinaEjercicio,
                                  RedirectAttributes redirectAttributes) {
        try {
            rutinaService.quitarEjercicio(idRutina, idRutinaEjercicio);
            mostrarMensaje(redirectAttributes, "todoOk", "rutina.ejercicio.quitado");
        } catch (Exception e) {
            mostrarMensaje(redirectAttributes, "error", "error.general");
        }

        redirectAttributes.addAttribute("cedulaCliente", cedulaCliente);
        return "redirect:/rutina/listado";
    }
    
    private void mostrarMensaje(RedirectAttributes redirectAttributes, String tipo, String llave) {
        String texto = messageSource.getMessage(llave, null, null, Locale.getDefault());
        if (texto == null) {
            texto = messageSource.getMessage("error.general", null, Locale.getDefault());
        }
        redirectAttributes.addFlashAttribute(tipo, texto);
    }
}
