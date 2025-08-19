package com.tuempresa.medicos.controller;

import com.tuempresa.medicos.model.Medico;
import com.tuempresa.medicos.service.MedicoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/medicos")
public class MedicoController {
    private final MedicoService service;
    public MedicoController(MedicoService service){ this.service = service; }

    @PostMapping public Medico crear(@RequestBody Medico m){ return service.crear(m); }
    @PutMapping("/{id}") public Optional<Medico> modificar(@PathVariable Long id, @RequestBody Medico d){ return service.modificar(id,d); }
    @DeleteMapping("/{id}") public void eliminar(@PathVariable Long id){ service.eliminar(id); }
    @GetMapping public List<Medico> listar(){ return service.listar(); }
    @GetMapping("/{id}") public ResponseEntity<Medico> obtener(@PathVariable Long id){
        return service.obtener(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
