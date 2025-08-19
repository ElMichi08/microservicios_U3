package com.tuempresa.medicos.controller;

import com.tuempresa.medicos.model.Slot;
import com.tuempresa.medicos.service.ReservaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/medicos/{medicoId}")
public class ReservaController {
    private final ReservaService service;
    public ReservaController(ReservaService service){ this.service = service; }

    @GetMapping("/disponibilidad")
    public List<Slot> disponibilidad(@PathVariable Long medicoId){
        return service.disponibilidad(medicoId);
    }

    @PostMapping("/reservas")
    public Slot reservar(@PathVariable Long medicoId, @RequestBody Map<String, Object> body){
        String inicio = (String) body.get("inicio");
        int min = body.get("duracionMinutos")==null? 30 : ((Number)body.get("duracionMinutos")).intValue();
        return service.reservar(medicoId, inicio, min);
    }

    @DeleteMapping("/reservas/{slotId}")
    public void liberar(@PathVariable Long medicoId, @PathVariable Long slotId){
        service.liberar(medicoId, slotId);
    }
}
