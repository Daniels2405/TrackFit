package com.TrackFit.repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.TrackFit.domain.Rutina;

@Repository
public interface RutinaRepository extends JpaRepository<Rutina, Integer>{
    Optional<Rutina> findFirstByClienteCedulaAndActivaTrueOrderByIdRutinaDesc(String cedula);
    
    List<Rutina> findByClienteCedulaAndActivaTrue(String cedula);
    
    List<Rutina> findByClienteCedulaOrderByIdRutinaDesc(String cedula);
}
