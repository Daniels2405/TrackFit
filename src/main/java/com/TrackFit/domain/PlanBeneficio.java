package com.TrackFit.domain;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
@Table(name = "plan_beneficio")
public class PlanBeneficio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficio")
    private Integer idBeneficio;
    // private Integer idPlan; ya no se usa, está dentro del objeto PlanMembresia

    @Column(name = "descripcion", nullable = false, length = 100)
    @NotBlank(message = "La descripción del beneficio no puede estar vacía")
    @Size(max = 100)
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "id_plan")
    private PlanMembresia planMembresia;

    public PlanMembresia getPlan() {
        return planMembresia;
    }

    public void setPlan(PlanMembresia plan) {
        this.planMembresia = plan;
    }
}
