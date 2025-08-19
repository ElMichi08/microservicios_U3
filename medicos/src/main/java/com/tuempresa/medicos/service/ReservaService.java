package com.tuempresa.medicos.service;

import com.tuempresa.medicos.model.Slot;
import com.tuempresa.medicos.repository.SlotRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final SlotRepository slots;
    public ReservaService(SlotRepository slots){ this.slots = slots; }

    public List<Slot> disponibilidad(Long medicoId){
        return slots.findByMedico(medicoId).stream()
                .sorted(Comparator.comparing(Slot::getInicio))
                .collect(Collectors.toList());
    }

    public Slot reservar(Long medicoId, String inicioIso, int duracionMin){
        LocalDateTime inicio = LocalDateTime.parse(inicioIso);

        // 1) Si ya existe un slot LIBRE con ese inicio, reservarlo
        Optional<Slot> libre = slots.findLibreByMedicoAndInicio(medicoId, inicio.toString());
        if (libre.isPresent()){
            Slot s = libre.get();
            s.setEstado("RESERVADO");
            return slots.save(s);
        }

        // 2) Crear slot EXTRA y reservar
        LocalDateTime fin = inicio.plusMinutes(duracionMin);
        Slot nuevo = new Slot(null, medicoId, inicio.toString(), fin.toString(), "RESERVADO", "EXTRA");
        return slots.save(nuevo);
    }

    public void liberar(Long medicoId, Long slotId){
        Slot s = slots.findById(slotId).orElseThrow(() -> new RuntimeException("Slot no encontrado"));
        if (!Objects.equals(s.getMedicoId(), medicoId)) throw new RuntimeException("Slot no pertenece al médico");
        s.setEstado("LIBRE");
        slots.save(s);
    }
}
