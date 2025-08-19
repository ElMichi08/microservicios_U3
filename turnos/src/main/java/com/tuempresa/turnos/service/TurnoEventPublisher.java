package com.tuempresa.turnos.service;

import com.tuempresa.turnos.config.RabbitMQConfig;
import com.tuempresa.turnos.model.Turno;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TurnoEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    public TurnoEventPublisher(RabbitTemplate rabbitTemplate){ this.rabbitTemplate = rabbitTemplate; }

    private void publicar(Turno turno, String accion){ // accion: AGENDADA | MODIFICADA | ANULADA
        Map<String, Object> evento = new HashMap<>();
        evento.put("pacienteId", turno.getPacienteId());
        evento.put("mensaje", mensaje(turno, accion));
        evento.put("tipo", accion); // el listener usa 'tipo' para registrar
        rabbitTemplate.convertAndSend(RabbitMQConfig.CITA_CONFIRMADA_EXCHANGE,
                                      RabbitMQConfig.CITA_CONFIRMADA_ROUTING_KEY,
                                      evento);
    }
    private String mensaje(Turno t, String accion){
        String f = t.getFechaHora().toString();
        switch (accion){
            case "AGENDADA": return "Su cita ha sido agendada para el " + f;
            case "MODIFICADA": return "Su cita ha sido modificada. Nueva fecha/hora: " + f;
            case "ANULADA": return "Su cita ha sido anulada.";
            default: return "Actualización de cita.";
        }
    }
    public void publicarAgendada(Turno t){ publicar(t, "AGENDADA"); }
    public void publicarModificada(Turno t){ publicar(t, "MODIFICADA"); }
    public void publicarAnulada(Turno t){ publicar(t, "ANULADA"); }
}
