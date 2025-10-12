package it.paoloadesso.gestionetavoli.controllers;

import it.paoloadesso.gestionetavoli.dto.*;
import it.paoloadesso.gestionetavoli.services.OrdineService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ordini")
@Validated
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @PostMapping("/chiudi/{idOrdine}")
    public ResponseEntity<StatoOrdineETavoloResponseDTO> chiudiOrdine(
            @PathVariable @Positive Long idOrdine) {
        return ResponseEntity.ok(ordineService.chiudiOrdine(idOrdine));
    }

}
