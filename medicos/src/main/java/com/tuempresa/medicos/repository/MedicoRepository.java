package com.tuempresa.medicos.repository;

import com.tuempresa.medicos.model.Medico;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class MedicoRepository {
    private final Map<Long, Medico> medicos = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Medico save(Medico m){ if(m.getId()==null) m.setId(seq.getAndIncrement()); medicos.put(m.getId(), m); return m; }
    public Optional<Medico> findById(Long id){ return Optional.ofNullable(medicos.get(id)); }
    public List<Medico> findAll(){ return new ArrayList<>(medicos.values()); }
    public void deleteById(Long id){ medicos.remove(id); }
}
