
package com.TrackFit.domain;
import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "ejercicio")
public class Ejercicio implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ejercicio")
    private Integer idEjercicio;

    @Column(name = "nombre", unique = true, nullable = false, length = 60)
    @NotBlank(message = "El nombre del ejercicio no puede estar vacío")
    @Size(max = 60)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    @Size(max = 255)
    private String descripcion;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;
}
