/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.TrackFit.service;

import com.TrackFit.domain.Reserva;
import com.TrackFit.domain.Horario;
import com.TrackFit.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.TrackFit.domain.Usuario;
import com.TrackFit.domain.Membresia;
import com.TrackFit.domain.PlanMembresia;
import com.TrackFit.repository.ClienteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

//Servicio encargado de validar y guardar las reservas
@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final HorarioService horarioService;
    private final ClienteRepository clienteRepository;
    private final MembresiaService membresiaService;
    private final MessageSource messageSource;

    //Método para registrar una nueva reserva
    @Transactional
    public void save(Reserva reserva) {

        //Verifica que se haya seleccionado un horario
        if (reserva.getHorario() == null
                || reserva.getHorario().getIdHorario() == null) {
            throw new IllegalArgumentException(
                    "Debe seleccionar un horario");
        }

        //Busca el horario registrado en la base de datos
        Horario horario = horarioService.getHorario(
                reserva.getHorario().getIdHorario());

        //Verifica que el horario exista
        if (horario == null) {
            throw new IllegalArgumentException(
                    "El horario seleccionado no existe");
        }

        //Consulta cuántos espacios quedan disponibles
        int disponibles
                = horarioService.getEspaciosDisponibles(horario);

        //Impide registrar una reserva cuando el cupo está lleno
        if (disponibles <= 0) {
            throw new IllegalArgumentException(
                    "No hay espacios disponibles para esta actividad");
        }

        //Asigna el horario existente a la reserva
        reserva.setHorario(horario);

        //Registra la reserva como confirmada
        reserva.setEstado("Confirmada");

        //Guarda la reserva en la base de datos
        reservaRepository.save(reserva);
    }
    // Membresía vigente del cliente (para saber su plan)
    @Transactional(readOnly = true)
    public Optional<Membresia> getMembresiaVigente(String cedulaCliente) {
        return membresiaService.getMembresiaVigente(cedulaCliente);
    }

    // Horarios que el cliente puede ver según las restricciones de su plan
    @Transactional(readOnly = true)
    public List<Horario> getHorariosDisponibles(String cedulaCliente) {
        List<Horario> resultado = new ArrayList<>();

        // Sin membresía vigente no hay plan, entonces no hay actividades
        Optional<Membresia> membresia = membresiaService.getMembresiaVigente(cedulaCliente);
        if (membresia.isEmpty()) {
            return resultado;
        }

        PlanMembresia plan = membresia.get().getPlanMembresia();

        // Restricción 1: solo desde hoy hasta los días de anticipación del plan
        LocalDate hoy = LocalDate.now();
        LocalDate hasta = hoy.plusDays(plan.getAnticipacionReservaDias());

        // Restricción 2: solo las actividades que su plan permite
        List<Integer> permitidas = reservaRepository.actividadesPermitidas(plan.getIdPlan());

        for (Horario h : reservaRepository.buscarHorarios(hoy, hasta)) {
            boolean actividadPermitida = permitidas.contains(h.getActividad().getIdActividad());
            if (actividadPermitida && !yaInicio(h)) {
                resultado.add(h);
            }
        }
        return resultado;
    }

    // Espacios disponibles de cada horario: { idHorario -> espacios libres }
    @Transactional(readOnly = true)
    public Map<Integer, Integer> getCuposDisponibles(List<Horario> horarios) {
        Map<Integer, Integer> cupos = new HashMap<>();
        for (Horario h : horarios) {
            cupos.put(h.getIdHorario(), horarioService.getEspaciosDisponibles(h));
        }
        return cupos;
    }

    // Reservas del cliente (para "Mis reservas")
    @Transactional(readOnly = true)
    public List<Reserva> getReservasCliente(String cedulaCliente) {
        return reservaRepository.findByClienteCedulaOrderByHorarioFechaAscHorarioHoraInicioAsc(cedulaCliente);
    }
    
    @Transactional
    public void reservar(String cedulaCliente, Integer idHorario) {

        // El cliente debe existir y tener el rol CLIENTE
        Usuario cliente = clienteRepository.findClienteByCedula(cedulaCliente)
                .orElseThrow(() -> error("membresia.error.cliente.invalido"));

        // El horario debe existir
        Horario horario = (idHorario == null) ? null : horarioService.getHorario(idHorario);
        if (horario == null) {
            throw error("error.general");
        }

        // Validación 1: la sesión no puede haber iniciado
        if (yaInicio(horario)) {
            throw error("reserva.error.horario.pasado");
        }

        // Validación 2: la membresía debe estar vigente
        Membresia membresia = membresiaService.getMembresiaVigente(cedulaCliente)
                .orElseThrow(() -> error("reserva.error.sin.membresia"));
        PlanMembresia plan = membresia.getPlanMembresia();

        // Validación 3: el plan debe permitir la actividad
        List<Integer> permitidas = reservaRepository.actividadesPermitidas(plan.getIdPlan());
        if (!permitidas.contains(horario.getActividad().getIdActividad())) {
            throw error("reserva.error.plan.no.permitido");
        }

        // Validación 4: respetar los días de anticipación del plan
        LocalDate limite = LocalDate.now().plusDays(plan.getAnticipacionReservaDias());
        if (horario.getFecha().isAfter(limite)) {
            throw error("reserva.error.anticipacion", plan.getAnticipacionReservaDias());
        }

        // Validación 5: no reservar dos veces el mismo horario
        Reserva reserva = reservaRepository
                .findByClienteCedulaAndHorarioIdHorario(cedulaCliente, idHorario)
                .orElse(null);

        if (reserva != null && "Confirmada".equals(reserva.getEstado())) {
            throw error("reserva.error.ya.reservada");
        }

        // Si la había cancelado, se reutiliza la misma fila; si no, se crea una nueva
        if (reserva == null) {
            reserva = new Reserva();
            reserva.setCliente(cliente);
        }
        reserva.setHorario(horario);

        save(reserva);
    }

    // HU13: el cliente cancela una de sus reservas confirmadas.
    // El espacio queda libre de inmediato, porque los cupos solo cuentan las reservas Confirmadas.
    @Transactional
    public void cancelar(String cedulaCliente, Integer idReserva) {

        // La reserva debe existir y pertenecer al cliente que la cancela
        Reserva reserva = (idReserva == null) ? null
                : reservaRepository.findById(idReserva).orElse(null);
        if (reserva == null
                || reserva.getCliente() == null
                || !reserva.getCliente().getCedula().equals(cedulaCliente)) {
            throw error("reserva.error.no.existe");
        }

        // Solo se cancelan reservas que siguen confirmadas
        if (!"Confirmada".equals(reserva.getEstado())) {
            throw error("reserva.error.ya.cancelada");
        }

        // No se puede cancelar una sesión que ya empezó o ya pasó
        if (yaInicio(reserva.getHorario())) {
            throw error("reserva.error.cancelar.pasado");
        }

        reserva.setEstado("Cancelada");
        reservaRepository.save(reserva);
    }

    // HU13: ids de las reservas que todavía se pueden cancelar (para mostrar el botón)
    public Set<Integer> getReservasCancelables(List<Reserva> reservas) {
        Set<Integer> cancelables = new HashSet<>();
        for (Reserva r : reservas) {
            if ("Confirmada".equals(r.getEstado()) && !yaInicio(r.getHorario())) {
                cancelables.add(r.getIdReserva());
            }
        }
        return cancelables;
    }

    // Crea un error con el texto ya traducido desde messages.properties
    private IllegalArgumentException error(String llave, Object... valores) {
        String texto = messageSource.getMessage(llave, valores, LocaleContextHolder.getLocale());
        return new IllegalArgumentException(texto);
    }

    // ¿La sesión ya empezó o ya pasó?
    private boolean yaInicio(Horario h) {
        LocalDateTime inicio = LocalDateTime.of(h.getFecha(), h.getHoraInicio());
        return inicio.isBefore(LocalDateTime.now());
    }
}
