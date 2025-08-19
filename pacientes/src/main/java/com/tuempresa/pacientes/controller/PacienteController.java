package com.tuempresa.pacientes.controller;

import com.tuempresa.pacientes.model.Paciente;
import com.tuempresa.pacientes.service.PacienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pacientes")
public class PacienteController {
    private final PacienteService service;
    public PacienteController(PacienteService service){ this.service = service; }

    @PostMapping public Paciente crear(@RequestBody Paciente p){ return service.crear(p); }
    @PutMapping("/{id}") public Optional<Paciente> modificar(@PathVariable Long id, @RequestBody Paciente d){ return service.modificar(id,d); }
    @DeleteMapping("/{id}") public void eliminar(@PathVariable Long id){ service.eliminar(id); }
    @GetMapping public List<Paciente> listar(){ return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Paciente> obtener(@PathVariable Long id){
        return service.obtener(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
