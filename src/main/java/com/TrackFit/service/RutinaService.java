package com.TrackFit.service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TrackFit.domain.Ejercicio;
import com.TrackFit.domain.Rutina;
import com.TrackFit.domain.RutinaEjercicio;
import com.TrackFit.domain.Usuario;
import com.TrackFit.repository.ClienteRepository;
import com.TrackFit.repository.EjercicioRepository;
import com.TrackFit.repository.EntrenadorRepository;
import com.TrackFit.repository.RutinaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RutinaService {

    private final RutinaRepository rutinaRepository;
    private final EjercicioRepository ejercicioRepository;
    private final ClienteRepository clienteRepository;
    private final EntrenadorRepository entrenadorRepository;

    @Transactional(readOnly = true)
    public List<Ejercicio> getEjercicios() {
        return ejercicioRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Usuario> getEntrenadores() {
        return entrenadorRepository.findEntrenadoresActivos();
    }

    @Transactional(readOnly = true)
    public Optional<Rutina> getRutina(Integer idRutina) {
        return rutinaRepository.findById(idRutina);
    }

    @Transactional(readOnly = true)
    public List<Rutina> getRutinasPorCliente(String cedulaCliente) {
        return rutinaRepository.findByClienteCedulaOrderByIdRutinaDesc(cedulaCliente);
    }

    @Transactional(readOnly = true)
    public Optional<Rutina> getRutinaVigente(String cedulaCliente) {
        return rutinaRepository.findFirstByClienteCedulaAndActivaTrueOrderByIdRutinaDesc(cedulaCliente);
    }

    @Transactional
    public Rutina crearRutina(String cedulaCliente, String cedulaEntrenador,
                              String nombre, String observaciones) {

        // Regla 1: el cliente debe existir y tener el rol CLIENTE
        Usuario cliente = clienteRepository.findClienteByCedula(cedulaCliente)
                .orElseThrow(() -> new IllegalArgumentException("rutina.error.cliente"));

        // Regla 2: quien la crea debe ser un entrenador
        Usuario entrenador = entrenadorRepository.findEntrenadorByCedula(cedulaEntrenador)
                .orElseThrow(() -> new IllegalArgumentException("rutina.error.entrenador"));

        // Regla 3: la rutina debe tener nombre
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("rutina.error.nombre");
        }

        // Regla 4: el cliente solo tiene una rutina vigente, así que se desactivan las anteriores
        List<Rutina> anteriores = rutinaRepository.findByClienteCedulaAndActivaTrue(cedulaCliente);
        for (Rutina anterior : anteriores) {
            anterior.setActiva(false);
            rutinaRepository.save(anterior);
        }

        Rutina rutina = new Rutina();
        rutina.setCliente(cliente);
        rutina.setEntrenador(entrenador);
        rutina.setNombre(nombre.trim());
        rutina.setObservaciones(observaciones);
        rutina.setActiva(true);

        return rutinaRepository.save(rutina);
    }
    
    @Transactional
    public void agregarEjercicio(Integer idRutina, Integer idEjercicio,
                                 Integer series, Integer repeticiones,
                                 BigDecimal peso, String observaciones) {

        Rutina rutina = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new IllegalArgumentException("rutina.error.no.existe"));

        Ejercicio ejercicio = ejercicioRepository.findById(idEjercicio)
                .orElseThrow(() -> new IllegalArgumentException("rutina.error.ejercicio"));

        // Series y repeticiones obligatorias y mayores a 0
        if (series == null || series < 1 || repeticiones == null || repeticiones < 1) {
            throw new IllegalArgumentException("rutina.error.series.repeticiones");
        }

        // El peso es opcional, pero no puede ser negativo
        if (peso != null && peso.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("rutina.error.peso");
        }

        // No se puede repetir el mismo ejercicio en la misma rutina
        for (RutinaEjercicio existente : rutina.getEjercicios()) {
            if (existente.getEjercicio().getIdEjercicio().equals(idEjercicio)) {
                throw new IllegalArgumentException("rutina.error.ejercicio.repetido");
            }
        }

        RutinaEjercicio detalle = new RutinaEjercicio();
        detalle.setEjercicio(ejercicio);
        detalle.setSeries(series);
        detalle.setRepeticiones(repeticiones);
        detalle.setPeso(peso);
        detalle.setObservaciones(observaciones);

        rutina.agregarEjercicio(detalle);
        rutinaRepository.save(rutina);
    }
    
    @Transactional
    public void quitarEjercicio(Integer idRutina, Integer idRutinaEjercicio) {
        Rutina rutina = rutinaRepository.findById(idRutina)
                .orElseThrow(() -> new IllegalArgumentException("rutina.error.no.existe"));

        rutina.getEjercicios().removeIf(d -> d.getIdRutinaEjercicio().equals(idRutinaEjercicio));
        rutinaRepository.save(rutina);
    }
}
