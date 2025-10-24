package it.paoloadesso.gestionalesala.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.services.CassaService;
import it.paoloadesso.gestionalesala.services.TavoliService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestione-tavoli")
@Validated
@Tag(name = "Gestione Tavoli", description = "API per la gestione dei tavoli")
public class TavoliController {

    private final CassaService cassaService;
    private final TavoliService tavoliService;

    public TavoliController(CassaService cassaService, TavoliService tavoliService) {
        this.cassaService = cassaService;
        this.tavoliService = tavoliService;
    }

    @Operation(
            summary = "Recupera tutti i tavoli aperti",
            description = "Restituisce la lista dei tavoli con stato OCCUPATO o RISERVATO. " +
                    "Utile per la cassa per vedere rapidamente quali tavoli hanno ordini attivi da chiudere."
    )
    @GetMapping("/tavoli-aperti")
    public ResponseEntity<List<TavoloApertoDTO>> getTavoliAperti() {
        return ResponseEntity.ok(cassaService.getTavoliAperti());
    }

    @Operation(
            summary = "Recupera dettagli completi dei tavoli aperti",
            description = "Restituisce i tavoli aperti con tutti i dettagli degli ordini associati: " +
                    "prodotti, quantità, prezzi e stato di pagamento. " +
                    "Utile per la cassa per preparare il conto completo di ogni tavolo."
    )
    @GetMapping("/dettaglio-tavoli-aperti")
    public ResponseEntity<List<TavoloApertoConDettagliOrdineDTO>> getTavoliApertiConDettagliOrdini() {
        return ResponseEntity.ok(cassaService.getTavoliApertiConDettagliOrdini());
    }

    @Operation(
            summary = "Recupera tutti i tavoli eliminati",
            description = "Restituisce la lista completa di tutti i tavoli cancellati (soft delete). " +
                    "Utile per operazioni di audit, controllo o eventuale ripristino. " +
                    "I tavoli eliminati non sono visibili nelle normali operazioni di gestione."
    )
    @GetMapping("/eliminati")
    public ResponseEntity<List<TavoliDTO>> getAllTavoliEliminati() {
        return ResponseEntity.ok(tavoliService.getAllTavoliEliminati());
    }

    @Operation(
            summary = "Recupera tutti i tavoli attivi",
            description = "Restituisce la lista completa di tutti i tavoli non eliminati (attivi) " +
                    "presenti nel ristorante con informazioni su numero/nome e stato. " +
                    "Utile per visualizzare tutti i tavoli disponibili per il servizio."
    )
    @GetMapping("/attivi")
    public ResponseEntity<List<TavoliDTO>> getAllTavoliAttivi() {
        return ResponseEntity.ok(tavoliService.getAllTavoliAttivi());
    }

    @Operation(
            summary = "Libera tutti i tavoli manualmente",
            description = "Reimposta manualmente lo stato di tutti i tavoli a LIBERO. " +
                    "Utile a fine turno per preparare velocemente la sala per il servizio successivo " +
                    "o in caso di necessità urgente. Nota: questa operazione avviene anche " +
                    "automaticamente ogni giorno all'orario di chiusura turno configurato."
    )
    @PatchMapping("/libera-tutti-i-tavoli")
    public ResponseEntity<String> liberaTuttiITavoli() {
        tavoliService.liberaTuttiITavoli();
        return ResponseEntity.ok("Lo stato di tutti i tavoli è stato reimpostato a «LIBERO».");
    }

    @Operation(
            summary = "Ripristina un tavolo eliminato",
            description = "Riattiva un tavolo precedentemente soft-deleted rendendolo nuovamente disponibile nelle operazioni di sala. Utile per ripristinare tavoli eliminati per errore o in seguito a manutenzioni."
    )
    @PatchMapping("/{idTavolo}/ripristina")
    public ResponseEntity<TavoliConDettaglioDeleteDTO> ripristinaSingoloTavolo(@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(tavoliService.ripristinaSingoloTavolo(idTavolo));
    }

    @Operation(
            summary = "Elimina un tavolo (soft delete)",
            description = "Elimina un tavolo e tutti gli ordini ad esso collegati."
    )
    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Elimina un tavolo (hard delete)",
            description = "Elimina definitivamente un tavolo e tutti gli ordini ad esso collegati. " +
                    "ATTENZIONE: questa operazione è irreversibile e rimuove anche lo storico degli ordini del tavolo. " +
                    "Usare solo per tavoli che non esistono più fisicamente nel ristorante."
    )
    @DeleteMapping("/hard-delete/{idTavolo}")
    public ResponseEntity<Void> hardDeleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaFisicamenteTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Modifica un tavolo esistente",
            description = "Aggiorna i dati di un tavolo (nome/numero, stato) con risultato dettagliato. " +
                    "Supporta modifiche parziali e fornisce informazioni sui campi effettivamente modificati."
    )
    @PutMapping("/{tavoloId}")
    public ResponseEntity<RisultatoModificaTavoloDTO> modificaTavolo(
            @PathVariable @Positive Long tavoloId,
            @RequestBody @Valid ModificaTavoloRequestDTO modificaDto) {

        RisultatoModificaTavoloDTO risultato = tavoliService.modificaTavolo(tavoloId, modificaDto);
        return ResponseEntity.ok(risultato);
    }

    // TODO: data ordine. (cronjob) eliminare ordini del giorno precedente

    // TODO: Logger

}
