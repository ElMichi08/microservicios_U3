package com.tuempresa.pacientes.repository;

import com.tuempresa.pacientes.model.Paciente;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class PacienteRepository {
    private final Map<Long, Paciente> data = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Optional<Paciente> findById(Long id){ return Optional.ofNullable(data.get(id)); }
    public List<Paciente> findAll(){ return new ArrayList<>(data.values()); }
    public Paciente save(Paciente p){
        if (p.getId()==null) p.setId(seq.getAndIncrement());
        data.put(p.getId(), p);
        return p;
    }
    public void deleteById(Long id){ data.remove(id); }
}
