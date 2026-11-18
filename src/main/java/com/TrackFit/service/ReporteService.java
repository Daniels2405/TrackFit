package com.TrackFit.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.TrackFit.domain.Horario;
import com.TrackFit.domain.Membresia;
import com.TrackFit.domain.Pago;
import com.TrackFit.domain.Reserva;
import com.TrackFit.repository.ReservaRepository;

import lombok.RequiredArgsConstructor;

/**
 * HU19: reportes de membresías, pagos y reservas.
 * Los cálculos se hacen en Java sobre los datos existentes; no se guarda nada nuevo.
 */
@Service
@RequiredArgsConstructor
public class ReporteService {

    private final MembresiaService membresiaService;
    private final PagoService pagoService;
    private final HorarioService horarioService;
    private final ReservaRepository reservaRepository;

    // ------------------------------------------------------------------
    // MEMBRESÍAS (estado actual, no depende del rango de fechas)
    // ------------------------------------------------------------------

    // Cantidad de membresías por estado: vigentes, vencidas, canceladas y total
    @Transactional(readOnly = true)
    public Map<String, Long> resumenMembresias() {
        long vigentes = 0;
        long vencidas = 0;
        long canceladas = 0;

        for (Membresia m : membresiaService.getMembresias()) {
            if ("Cancelada".equalsIgnoreCase(m.getEstado())) {
                canceladas++;
            } else if (m.isVigente()) {
                vigentes++;
            } else {
                vencidas++;
            }
        }

        Map<String, Long> resumen = new LinkedHashMap<>();
        resumen.put("vigentes", vigentes);
        resumen.put("vencidas", vencidas);
        resumen.put("canceladas", canceladas);
        resumen.put("total", vigentes + vencidas + canceladas);
        return resumen;
    }

    // Membresías vigentes agrupadas por plan
    @Transactional(readOnly = true)
    public List<ReporteFila> membresiasVigentesPorPlan() {
        Map<String, ReporteFila> filas = new LinkedHashMap<>();
        long total = 0;

        for (Membresia m : membresiaService.getMembresias()) {
            if (m.isVigente()) {
                String plan = m.getPlanMembresia().getNombre();
                filas.computeIfAbsent(plan, ReporteFila::new).sumar(null);
                total++;
            }
        }
        return ordenarConPorcentaje(filas, total);
    }

    // ------------------------------------------------------------------
    // PAGOS (dentro del rango de fechas)
    // ------------------------------------------------------------------

    @Transactional(readOnly = true)
    public List<Pago> getPagos(LocalDate desde, LocalDate hasta) {
        List<Pago> resultado = new ArrayList<>();
        for (Pago p : pagoService.getPagos()) {
            if (p.getFecha() != null && dentroDelRango(p.getFecha(), desde, hasta)) {
                resultado.add(p);
            }
        }
        resultado.sort(Comparator.comparing(Pago::getFecha).reversed());
        return resultado;
    }

    // Suma de los montos de una lista de pagos
    public BigDecimal totalPagos(List<Pago> pagos) {
        BigDecimal total = BigDecimal.ZERO;
        for (Pago p : pagos) {
            if (p.getMonto() != null) {
                total = total.add(p.getMonto());
            }
        }
        return total;
    }

    // Pagos agrupados por método (Efectivo, Tarjeta, SINPE, Transferencia)
    public List<ReporteFila> pagosPorMetodo(List<Pago> pagos) {
        Map<String, ReporteFila> filas = new LinkedHashMap<>();
        for (Pago p : pagos) {
            filas.computeIfAbsent(p.getMetodoPago(), ReporteFila::new).sumar(p.getMonto());
        }
        return ordenarPorMonto(filas, totalPagos(pagos));
    }

    // Ingresos agrupados por plan de membresía
    public List<ReporteFila> pagosPorPlan(List<Pago> pagos) {
        Map<String, ReporteFila> filas = new LinkedHashMap<>();
        for (Pago p : pagos) {
            String plan = p.getMembresia().getPlanMembresia().getNombre();
            filas.computeIfAbsent(plan, ReporteFila::new).sumar(p.getMonto());
        }
        return ordenarPorMonto(filas, totalPagos(pagos));
    }

    // ------------------------------------------------------------------
    // RESERVAS (de sesiones cuya fecha está dentro del rango)
    // ------------------------------------------------------------------

    // Cantidad de reservas confirmadas, canceladas y total
    @Transactional(readOnly = true)
    public Map<String, Long> resumenReservas(LocalDate desde, LocalDate hasta) {
        long confirmadas = 0;
        long canceladas = 0;

        for (Reserva r : reservaRepository.findAll()) {
            if (dentroDelRango(r.getHorario().getFecha(), desde, hasta)) {
                if ("Confirmada".equals(r.getEstado())) {
                    confirmadas++;
                } else {
                    canceladas++;
                }
            }
        }

        Map<String, Long> resumen = new LinkedHashMap<>();
        resumen.put("confirmadas", confirmadas);
        resumen.put("canceladas", canceladas);
        resumen.put("total", confirmadas + canceladas);
        return resumen;
    }

    // Reservas confirmadas por actividad y ocupación (confirmadas / cupos ofrecidos)
    @Transactional(readOnly = true)
    public List<ReporteFila> ocupacionPorActividad(LocalDate desde, LocalDate hasta) {
        Map<String, ReporteFila> filas = new LinkedHashMap<>();

        // Cupos ofrecidos: suma del cupo máximo de las sesiones del rango
        for (Horario h : horarioService.getHorarios()) {
            if (dentroDelRango(h.getFecha(), desde, hasta)) {
                filas.computeIfAbsent(h.getActividad().getNombre(), ReporteFila::new)
                        .sumarCapacidad(h.getCupoMaximo());
            }
        }

        // Reservas confirmadas de esas sesiones
        for (Reserva r : reservaRepository.findAll()) {
            Horario h = r.getHorario();
            if ("Confirmada".equals(r.getEstado()) && dentroDelRango(h.getFecha(), desde, hasta)) {
                filas.computeIfAbsent(h.getActividad().getNombre(), ReporteFila::new).sumar(null);
            }
        }

        List<ReporteFila> resultado = new ArrayList<>(filas.values());
        for (ReporteFila f : resultado) {
            f.calcularPorcentaje(f.getCantidad(), f.getCapacidad());
        }
        resultado.sort(Comparator.comparing(ReporteFila::getCantidad).reversed());
        return resultado;
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------

    private boolean dentroDelRango(LocalDate fecha, LocalDate desde, LocalDate hasta) {
        return fecha != null && !fecha.isBefore(desde) && !fecha.isAfter(hasta);
    }

    // Ordena por cantidad (mayor primero) y calcula el porcentaje de cada fila
    private List<ReporteFila> ordenarConPorcentaje(Map<String, ReporteFila> filas, long total) {
        List<ReporteFila> resultado = new ArrayList<>(filas.values());
        for (ReporteFila f : resultado) {
            f.calcularPorcentaje(f.getCantidad(), total);
        }
        resultado.sort(Comparator.comparing(ReporteFila::getCantidad).reversed());
        return resultado;
    }

    // Ordena por monto (mayor primero) y calcula el porcentaje del total de ingresos
    private List<ReporteFila> ordenarPorMonto(Map<String, ReporteFila> filas, BigDecimal total) {
        List<ReporteFila> resultado = new ArrayList<>(filas.values());
        for (ReporteFila f : resultado) {
            f.calcularPorcentaje(f.getMonto().doubleValue(), total.doubleValue());
        }
        resultado.sort(Comparator.comparing(ReporteFila::getMonto).reversed());
        return resultado;
    }
}
