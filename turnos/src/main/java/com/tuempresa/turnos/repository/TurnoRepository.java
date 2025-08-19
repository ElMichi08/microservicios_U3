package com.tuempresa.turnos.repository;
import org.springframework.stereotype.Repository;

import com.tuempresa.turnos.model.Turno;
import java.util.*;

@Repository
public class TurnoRepository {
    private final Map<Long, Turno> turnos = new HashMap<>();
    private long idCounter = 1;

    public Turno save(Turno turno) {
        if (turno.getId() == null) {
            turno.setId(idCounter++);
        }
        turnos.put(turno.getId(), turno);
        return turno;
    }

    public Optional<Turno> findById(Long id) { return Optional.ofNullable(turnos.get(id)); }
    public List<Turno> findAll() { return new ArrayList<>(turnos.values()); }
    public void deleteById(Long id) { turnos.remove(id); }
}
