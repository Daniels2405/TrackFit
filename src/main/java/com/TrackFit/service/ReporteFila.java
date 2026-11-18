package com.TrackFit.service;

import java.math.BigDecimal;

/**
 * HU19: una fila de un reporte (por ejemplo, un plan, un método de pago o una actividad).
 * Guarda una etiqueta, una cantidad, un monto opcional y el porcentaje que
 * representa la fila dentro del total, para dibujar la barra en la vista.
 */
public class ReporteFila {

    private final String etiqueta;
    private long cantidad;
    private BigDecimal monto = BigDecimal.ZERO;
    private long capacidad;
    private double porcentaje;

    public ReporteFila(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    // Suma una ocurrencia (y su monto, si aplica)
    public void sumar(BigDecimal valor) {
        cantidad++;
        if (valor != null) {
            monto = monto.add(valor);
        }
    }

    // Suma capacidad (cupos) para calcular la ocupación de una actividad
    public void sumarCapacidad(int cupos) {
        capacidad += cupos;
    }

    // Calcula el porcentaje respecto a un total (0 si el total es 0)
    public void calcularPorcentaje(double parte, double total) {
        porcentaje = (total <= 0) ? 0 : Math.min(100.0, parte * 100.0 / total);
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public long getCantidad() {
        return cantidad;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public long getCapacidad() {
        return capacidad;
    }

    public double getPorcentaje() {
        return porcentaje;
    }

    // Porcentaje redondeado, para el ancho de la barra de progreso
    public int getPorcentajeEntero() {
        return (int) Math.round(porcentaje);
    }
}
