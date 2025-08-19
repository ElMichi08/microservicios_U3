package com.tuempresa.medicos.service;

import com.tuempresa.medicos.model.Medico;
import com.tuempresa.medicos.repository.MedicoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicoService {
    private final MedicoRepository repository;
    public MedicoService(MedicoRepository repository){ this.repository = repository; }

    public Medico crear(Medico m){ return repository.save(m); }
    public Optional<Medico> modificar(Long id, Medico d){
        return repository.findById(id).map(cur -> {
            cur.setNombre(d.getNombre());
            cur.setEspecialidad(d.getEspecialidad());
            return repository.save(cur);
        });
    }
    public boolean eliminar(Long id){ if(repository.findById(id).isPresent()){ repository.deleteById(id); return true; } return false; }
    public List<Medico> listar(){ return repository.findAll(); }
    public Optional<Medico> obtener(Long id){ return repository.findById(id); }
}
