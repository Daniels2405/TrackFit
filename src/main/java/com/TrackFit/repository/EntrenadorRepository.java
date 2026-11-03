package com.TrackFit.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.TrackFit.domain.Usuario;

@Repository
public interface EntrenadorRepository extends JpaRepository<Usuario, String>{
    @Query("SELECT DISTINCT u FROM Usuario u JOIN u.roles r WHERE r.rol = 'ENTRENADOR' AND u.activo = true")
    List<Usuario> findEntrenadoresActivos(); 
    
    @Query("SELECT u FROM Usuario u JOIN u.roles r WHERE u.cedula = :cedula AND r.rol = 'ENTRENADOR'")
    Optional<Usuario> findEntrenadorByCedula(@Param("cedula") String cedula);
}
