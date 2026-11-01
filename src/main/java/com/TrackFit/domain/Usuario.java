package com.TrackFit.domain;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
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
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "cedula", nullable = false, length = 20)
    @NotBlank(message = "La cédula no puede estar vacía")
    @Size(max = 20)
    private String cedula;

    @Column(name = "username", unique = true, nullable = false, length = 30)
    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 30)
    private String username;

    @Column(name = "password", nullable = false, length = 512)
    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(max = 512)
    private String password;

    @Column(name = "nombre", nullable = false, length = 30)
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(max = 30)
    private String nombre;

    @Column(name = "apellido1", nullable = false, length = 30)
    @NotBlank(message = "El primer apellido no puede estar vacío")
    @Size(max = 30)
    private String apellido1;

    @Column(name = "apellido2", length = 30)
    @Size(max = 30)
    private String apellido2;

    @Column(name = "correo", unique = true, nullable = false, length = 75)
    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 75)
    private String correo;

    @Column(name = "telefono", length = 25)
    @Size(max = 25)
    private String telefono;

    @Column(name = "especialidad", length = 60)
    @Size(max = 60)
    private String especialidad;

    @Column(name = "ruta_imagen_perfil", length = 1024)
    private String rutaImagenPerfil;

    private boolean activo;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "usuario_rol",
        joinColumns = @JoinColumn(name = "cedula"),
        inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private List<Rol> roles;

    @OneToMany(mappedBy = "usuario")
    private List<Membresia> membresias;

    public String getNombreCompleto() {
        StringBuilder sb = new StringBuilder();
        if (nombre != null) {
            sb.append(nombre);
        }
        if (apellido1 != null) {
            sb.append(" ").append(apellido1);
        }
        if (apellido2 != null && !apellido2.isBlank()) {
            sb.append(" ").append(apellido2);
        }
        return sb.toString().trim();
    }
}
