package com.tuempresa.pacientes.service;

import com.tuempresa.pacientes.model.Paciente;
import com.tuempresa.pacientes.repository.PacienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteService {
    private final PacienteRepository repository;
    public PacienteService(PacienteRepository repository){ this.repository = repository; }

    public Paciente crear(Paciente p){ return repository.save(p); }
    public Optional<Paciente> modificar(Long id, Paciente d){
        return repository.findById(id).map(cur -> {
            cur.setNombre(d.getNombre());
            cur.setEmail(d.getEmail());
            cur.setTelefono(d.getTelefono());
            return repository.save(cur);
        });
    }
    public boolean eliminar(Long id){
        if (repository.findById(id).isPresent()){ repository.deleteById(id); return true; }
        return false;
    }
    public List<Paciente> listar(){ return repository.findAll(); }
    public Optional<Paciente> obtener(Long id){ return repository.findById(id); }
}
