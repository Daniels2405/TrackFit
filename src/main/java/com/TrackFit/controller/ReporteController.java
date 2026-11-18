package com.TrackFit.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.TrackFit.domain.Pago;
import com.TrackFit.service.ReporteService;

import lombok.RequiredArgsConstructor;

// HU19: reportes de membresías, pagos y reservas
@Controller
@RequiredArgsConstructor
@RequestMapping("/reporte")
public class ReporteController {

    private final ReporteService reporteService;
    private final MessageSource messageSource;

    @GetMapping("/listado")
    public String listado(
            @RequestParam(value = "desde", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(value = "hasta", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            Model model) {

        // Por defecto se muestra el mes actual completo
        LocalDate hoy = LocalDate.now();
        if (desde == null) {
            desde = hoy.withDayOfMonth(1);
        }
        if (hasta == null) {
            hasta = hoy.withDayOfMonth(hoy.lengthOfMonth());
        }

        // Si las fechas vienen al revés, se intercambian y se avisa
        if (desde.isAfter(hasta)) {
            LocalDate temporal = desde;
            desde = hasta;
            hasta = temporal;
            model.addAttribute("error",
                    messageSource.getMessage("reporte.error.rango", null, LocaleContextHolder.getLocale()));
        }

        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);

        // Membresías (estado actual)
        model.addAttribute("membresias", reporteService.resumenMembresias());
        model.addAttribute("membresiasPorPlan", reporteService.membresiasVigentesPorPlan());

        // Pagos del rango
        List<Pago> pagos = reporteService.getPagos(desde, hasta);
        model.addAttribute("pagos", pagos);
        model.addAttribute("totalPagos", reporteService.totalPagos(pagos));
        model.addAttribute("pagosPorMetodo", reporteService.pagosPorMetodo(pagos));
        model.addAttribute("pagosPorPlan", reporteService.pagosPorPlan(pagos));

        // Reservas del rango
        model.addAttribute("reservas", reporteService.resumenReservas(desde, hasta));
        model.addAttribute("ocupacion", reporteService.ocupacionPorActividad(desde, hasta));

        return "reporte/listado";
    }
}
