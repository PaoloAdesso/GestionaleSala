package it.paoloadesso.gestionetavoli.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionetavoli.dto.*;
import it.paoloadesso.gestionetavoli.services.OrdineService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordini")
@Validated
@Tag(name = "Gestione Ordini", description = "API per la chiusura e consultazione storico ordini")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @Operation(
            summary = "Chiudi un ordine",
            description = "Chiude definitivamente un ordine impostando il suo stato a CHIUSO. " +
                    "Se tutti gli ordini del tavolo sono chiusi, il tavolo viene automaticamente " +
                    "liberato (stato LIBERO). Operazione tipicamente eseguita dalla cassa dopo " +
                    "il pagamento completo."
    )
    @PostMapping("/chiudi/{idOrdine}")
    public ResponseEntity<StatoOrdineETavoloResponseDTO> chiudiOrdine(
            @PathVariable @Positive Long idOrdine) {
        return ResponseEntity.ok(ordineService.chiudiOrdine(idOrdine));
    }

    @Operation(
            summary = "Recupera tutti gli ordini chiusi",
            description = "Restituisce la lista completa di tutti gli ordini con stato CHIUSO, " +
                    "indipendentemente dalla data. Include il dettaglio dei tavoli e dei prodotti ordinati. " +
                    "Utile per consultare lo storico completo delle vendite."
    )
    @GetMapping("/chiusi")
    public ResponseEntity<List<TavoloConOrdiniChiusiDTO>> getOrdiniChiusi() {
        return ResponseEntity.ok(ordineService.getOrdiniChiusi());
    }

    @Operation(
            summary = "Recupera ordini chiusi del turno corrente",
            description = "Restituisce gli ordini chiusi del turno lavorativo corrente con dettagli " +
                    "completi su tavoli e prodotti. Rispetta la logica del turno lavorativo: " +
                    "se richiesto dopo mezzanotte ma prima dell'orario di chiusura turno, " +
                    "restituisce gli ordini chiusi del giorno precedente. Utile per report di fine turno."
    )
    @GetMapping("/chiusi-oggi")
    public ResponseEntity<List<TavoloConOrdiniChiusiDTO>> getOrdiniChiusiDiOggi() {
        return ResponseEntity.ok(ordineService.getOrdiniChiusiDiOggi());
    }

}
