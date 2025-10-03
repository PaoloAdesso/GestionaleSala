package it.paoloadesso.gestionetavoli.controllers;

import it.paoloadesso.gestionetavoli.dto.TavoloApertoConDettagliOrdineDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoDTO;
import it.paoloadesso.gestionetavoli.services.CassaService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cassa")
@Validated
public class CassaController {

    private final CassaService cassaService;

    public CassaController(CassaService cassaService) {
        this.cassaService = cassaService;
    }

    @GetMapping("/tavoli-aperti")
    public ResponseEntity<List<TavoloApertoDTO>> getTavoliAperti() {
        return ResponseEntity.ok(cassaService.getTavoliAperti());
    }

    @GetMapping("/dettaglio-tavoli-aperti")
    public ResponseEntity<List<TavoloApertoConDettagliOrdineDTO>> getDettaglioTavoliAperti() {
        return ResponseEntity.ok(cassaService.getDettaglioTavoliAperti());
    }
}
