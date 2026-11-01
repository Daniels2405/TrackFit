package com.TrackFit.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    private Integer idRol;

    @Column(name = "rol", unique = true, nullable = false, length = 25)
    @NotBlank(message = "El nombre del rol no puede estar vacío")
    @Size(max = 25)
    private String rol;

    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;

    public Rol(String rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return rol;
    }

    public void setNombre(String nombre) {
        this.rol = nombre;
    }
}
