package com.TrackFit.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TrackFit.domain.Ejercicio;

@Repository
public interface EjercicioRepository extends JpaRepository<Ejercicio, Integer>{
    List<Ejercicio> findAllByOrderByNombreAsc();
}
