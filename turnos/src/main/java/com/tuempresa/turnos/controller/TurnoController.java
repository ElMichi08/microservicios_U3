package com.tuempresa.turnos.controller;

import com.tuempresa.turnos.model.Turno;
import com.tuempresa.turnos.service.TurnoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/turnos")
public class TurnoController {
    private final TurnoService service;

    public TurnoController(TurnoService service) {
        this.service = service;
    }

    // Agendar
    @PostMapping
    public ResponseEntity<Turno> crear(@RequestBody Turno datos){
        return ResponseEntity.ok(service.crear(datos));
    }

    // Modificar
    @PutMapping("/{id}")
    public ResponseEntity<Turno> modificar(@PathVariable Long id, @RequestBody Turno cambios){
        Optional<Turno> res = service.modificar(id, cambios);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Anular
    @DeleteMapping("/{id}")
    public ResponseEntity<Turno> anular(@PathVariable Long id, @RequestParam(required = false) String motivo){
        Optional<Turno> res = service.anular(id, motivo);
        return res.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Obtener por id
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtener(@PathVariable Long id){
        return service.obtenerTurno(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar con filtros
    @GetMapping
    public List<Turno> listar(@RequestParam(required = false) Long pacienteId,
                              @RequestParam(required = false) Long medicoId,
                              @RequestParam(required = false) String desde,
                              @RequestParam(required = false) String hasta,
                              @RequestParam(required = false) String estado){
        LocalDateTime d = (desde==null||desde.isBlank())? null : LocalDateTime.parse(desde);
        LocalDateTime h = (hasta==null||hasta.isBlank())? null : LocalDateTime.parse(hasta);
        return service.listarConFiltros(pacienteId, medicoId, d, h, estado);
    }
}
