package com.tuempresa.medicos.repository;

import com.tuempresa.medicos.model.Slot;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class SlotRepository {
    private final Map<Long, Slot> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public List<Slot> findByMedico(Long medicoId){
        return data.values().stream()
                .filter(s -> Objects.equals(s.getMedicoId(), medicoId))
                .sorted(Comparator.comparing(Slot::getInicio))
                .collect(Collectors.toList());
    }

    public Optional<Slot> findById(Long id){ return Optional.ofNullable(data.get(id)); }

    public Optional<Slot> findLibreByMedicoAndInicio(Long medicoId, String inicioIso){
        for (Slot s : data.values()){
            if (Objects.equals(s.getMedicoId(), medicoId)
                && "LIBRE".equals(s.getEstado())
                && Objects.equals(s.getInicio(), inicioIso)) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }

    public Slot save(Slot s){
        if (s.getId()==null) s.setId(seq.getAndIncrement());
        data.put(s.getId(), s);
        return s;
    }
}
