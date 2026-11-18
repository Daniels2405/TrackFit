package com.TrackFit.domain;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author danie
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "membresia")
public class Membresia implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_membresia")
    private Integer idMembresia;

    @ManyToOne
    @JoinColumn(name = "cedula_cliente")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private PlanMembresia planMembresia;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio", nullable = false)
    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_vencimiento", nullable = false)
    @NotNull(message = "La fecha de vencimiento no puede estar vacía")
    private LocalDate fechaVencimiento;

    @Column(name = "estado", nullable = false, length = 20)
    private String estado = "Activa";

    public Usuario getCliente() {
        return usuario;
    }

    public void setCliente(Usuario cliente) {
        this.usuario = cliente;
    }

    public PlanMembresia getPlan() {
        return planMembresia;
    }

    public void setPlan(PlanMembresia plan) {
        this.planMembresia = plan;
    }

    /**
     * Calcula dinámicamente si la membresía está vigente:
     * El estado no se almacena en base de datos; se calcula con las fechas.
     * Vigente = estado 'Activa' y fecha_inicio <= hoy <= fecha_vencimiento.
     * Como se calcula cada vez, no hace falta ninguna tarea programada:
     * el día de inicio pasa sola de Programada a Vigente, y luego a Vencida.
     */
    public boolean isVigente() {
        if (fechaInicio == null || fechaVencimiento == null || estado == null) {
            return false;
        }
        LocalDate hoy = LocalDate.now();
        return "Activa".equalsIgnoreCase(estado)
                && !fechaInicio.isAfter(hoy)
                && !fechaVencimiento.isBefore(hoy);
    }

    // Programada = estado 'Activa' pero todavía no llega su fecha de inicio (por ejemplo, una renovación)
    public boolean isProgramada() {
        return "Activa".equalsIgnoreCase(estado)
                && fechaInicio != null
                && fechaInicio.isAfter(LocalDate.now());
    }

    // Cancelada, Programada, Vigente o Vencida
    public String getEstadoCalculado() {
        if ("Cancelada".equalsIgnoreCase(estado)) {
            return "Cancelada";
        }
        if (isProgramada()) {
            return "Programada";
        }
        return isVigente() ? "Vigente" : "Vencida";
    }
}
