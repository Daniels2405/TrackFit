package com.TrackFit.domain;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Table(name = "rutina")
public class Rutina implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Integer idRutina;
    
    @ManyToOne
    @JoinColumn(name = "cedula_cliente", nullable = false)
    private Usuario cliente;
    
    @ManyToOne
    @JoinColumn(name = "cedula_entrenador", nullable = false)
    private Usuario entrenador;
    
    @Column(name = "nombre", nullable = false, length = 50)
    @NotBlank(message = "El nombre de la rutina no puede estar vacío")
    @Size(max = 50)
    private String nombre;
    
    @Column(name = "observaciones", length = 500)
    @Size(max = 500)
    private String observaciones;

    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;

    private boolean activa = true;

    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @OneToMany(mappedBy = "rutina", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RutinaEjercicio> ejercicios = new ArrayList<>();

    public void agregarEjercicio(RutinaEjercicio detalle) {
        detalle.setRutina(this);
        ejercicios.add(detalle);
    }
}
