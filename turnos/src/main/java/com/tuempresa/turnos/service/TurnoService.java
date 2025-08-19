package com.tuempresa.turnos.service;

import com.tuempresa.turnos.model.Turno;
import com.tuempresa.turnos.repository.TurnoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TurnoService {
    private final TurnoRepository repository;
    private final RestTemplate http = new RestTemplate();
    private final TurnoEventPublisher events;

    @Value("${pacientes.service.url}") private String pacientesUrl; // .../pacientes
    @Value("${medicos.service.url}")   private String medicosUrl;   // .../medicos

    public TurnoService(TurnoRepository repository, TurnoEventPublisher events){
        this.repository = repository;
        this.events = events;
    }

    // ---------- Validaciones ----------
    private void assertPaciente(Long id){
        try { http.getForObject(pacientesUrl + "/" + id, Object.class); }
        catch (HttpClientErrorException e){
            if (e.getStatusCode()==HttpStatus.NOT_FOUND) throw new IllegalArgumentException("Paciente no existe");
            throw e;
        }
    }
    private void assertMedico(Long id){
        try { http.getForObject(medicosUrl + "/" + id, Object.class); }
        catch (HttpClientErrorException e){
            if (e.getStatusCode()==HttpStatus.NOT_FOUND) throw new IllegalArgumentException("Médico no existe");
            throw e;
        }
    }

    // ---------- Integración con micro de médicos (reservas) ----------
    @SuppressWarnings("rawtypes")
    private Long reservarSlot(Long medicoId, LocalDateTime fechaHora){
        Map<String,Object> body = new HashMap<>();
        body.put("inicio", fechaHora.toString());  // ISO-8601
        body.put("duracionMinutos", 30);
        Map resp = http.postForObject(medicosUrl + "/" + medicoId + "/reservas", body, Map.class);
        if (resp==null || resp.get("id")==null) throw new RuntimeException("No se pudo reservar slot");
        return ((Number) resp.get("id")).longValue();
    }
    private void liberarSlot(Long medicoId, Long slotId){
        if (slotId == null) return;
        http.delete(medicosUrl + "/" + medicoId + "/reservas/" + slotId);
    }

    // =======================================================
    //                  Flujos de negocio
    // =======================================================

    // 1) Agendar
    public Turno crear(Turno datos){
        if (datos.getPacienteId()==null || datos.getMedicoId()==null || datos.getFechaHora()==null)
            throw new IllegalArgumentException("Faltan datos obligatorios: pacienteId, medicoId, fechaHora");

        assertPaciente(datos.getPacienteId());
        assertMedico(datos.getMedicoId());

        Long slotId = reservarSlot(datos.getMedicoId(), datos.getFechaHora());
        datos.setSlotId(slotId);
        datos.setEstado("AGENDADO");
        Turno creado = repository.save(datos);

        events.publicarAgendada(creado);
        return creado;
    }

    // 2) Modificar (reprogramar)
    public Optional<Turno> modificar(Long id, Turno cambios){
        return repository.findById(id).map(actual -> {
            // Valores previos (sirven para liberar el slot viejo)
            Long oldMedico = actual.getMedicoId();
            Long oldSlot   = actual.getSlotId();

            // Nuevos valores
            Long nuevoPaciente = cambios.getPacienteId()!=null ? cambios.getPacienteId() : actual.getPacienteId();
            Long nuevoMedico   = cambios.getMedicoId()!=null   ? cambios.getMedicoId()   : actual.getMedicoId();
            LocalDateTime nuevaFecha = cambios.getFechaHora()!=null ? cambios.getFechaHora() : actual.getFechaHora();

            // Validaciones si cambian referencias
            if (!Objects.equals(nuevoPaciente, actual.getPacienteId())) assertPaciente(nuevoPaciente);
            if (!Objects.equals(nuevoMedico, actual.getMedicoId()))     assertMedico(nuevoMedico);

            boolean requiereNuevaReserva =
                    !Objects.equals(nuevoMedico, actual.getMedicoId()) ||
                    !Objects.equals(nuevaFecha, actual.getFechaHora());

            Long nuevoSlot = actual.getSlotId();
            if (requiereNuevaReserva){
                nuevoSlot = reservarSlot(nuevoMedico, nuevaFecha);
            }

            // Persistir cambios
            actual.setPacienteId(nuevoPaciente);
            actual.setMedicoId(nuevoMedico);
            actual.setFechaHora(nuevaFecha);
            actual.setSlotId(nuevoSlot);
            actual.setEstado("AGENDADO");
            Turno mod = repository.save(actual);

            // Liberar slot anterior después de persistir
            if (requiereNuevaReserva){
                try { liberarSlot(oldMedico, oldSlot); } catch (Exception ignored){}
            }

            events.publicarModificada(mod);
            return mod;
        });
    }

    // 3) Anular
    public Optional<Turno> anular(Long id, String motivo){
        return repository.findById(id).map(actual -> {
            if (!"CANCELADO".equalsIgnoreCase(actual.getEstado())){
                actual.setEstado("CANCELADO");
                repository.save(actual);
                try { liberarSlot(actual.getMedicoId(), actual.getSlotId()); } catch (Exception ignored){}
                events.publicarAnulada(actual);
            }
            return actual;
        });
    }

    // 4) Consultas
    public Optional<Turno> obtenerTurno(Long id){ return repository.findById(id); }

    public List<Turno> listarTurnos(){
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Turno::getFechaHora))
                .collect(Collectors.toList());
    }

    public List<Turno> listarConFiltros(Long pacienteId, Long medicoId,
                                        LocalDateTime desde, LocalDateTime hasta, String estado){
        return repository.findAll().stream()
                .filter(t -> pacienteId==null || Objects.equals(t.getPacienteId(), pacienteId))
                .filter(t -> medicoId==null   || Objects.equals(t.getMedicoId(), medicoId))
                .filter(t -> estado==null     || estado.equalsIgnoreCase(t.getEstado()))
                .filter(t -> desde==null      || !t.getFechaHora().isBefore(desde))
                .filter(t -> hasta==null      || !t.getFechaHora().isAfter(hasta))
                .sorted(Comparator.comparing(Turno::getFechaHora))
                .collect(Collectors.toList());
    }
}
