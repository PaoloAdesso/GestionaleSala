package it.paoloadesso.gestionetavoli.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionetavoli.dto.TavoliDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoConDettagliOrdineDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoDTO;
import it.paoloadesso.gestionetavoli.services.CassaService;
import it.paoloadesso.gestionetavoli.services.TavoliService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestione-sala")
@Validated
@Tag(name = "Gestione Sala", description = "API per la gestione della sala dalla cassa")
public class CassaController {

    private final CassaService cassaService;
    private final TavoliService tavoliService;

    public CassaController(CassaService cassaService, TavoliService tavoliService) {
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
            summary = "Aggiorna lo stato di un tavolo",
            description = "Permette di modificare lo stato di un tavolo (LIBERO, OCCUPATO, RISERVATO) " +
                    "o altre informazioni. Supporta aggiornamenti parziali: invia solo i campi da modificare. " +
                    "Utile per la cassa quando chiude un tavolo o cambia una prenotazione."
    )
    @PutMapping("/{id}")
    public ResponseEntity<TavoliDTO> aggiornaTavolo(
            @PathVariable Long id,
            @RequestBody @Valid TavoliDTO tavolo
    ) {
        return ResponseEntity.ok(tavoliService.aggiornaTavolo(id, tavolo));
    }

    @Operation(
            summary = "Elimina un tavolo",
            description = "Elimina definitivamente un tavolo e tutti gli ordini ad esso collegati. " +
                    "ATTENZIONE: questa operazione è irreversibile e rimuove anche lo storico degli ordini del tavolo. " +
                    "Usare solo per tavoli che non esistono più fisicamente nel ristorante."
    )
    @DeleteMapping("/{idTavolo}")
    public ResponseEntity<Void> deleteTavolo(@PathVariable Long idTavolo){
        tavoliService.eliminaTavoloByIdERelativiOrdiniCollegati(idTavolo);
        return ResponseEntity.noContent().build();
    }

    /*
        TODO: Imposta lo stato di tutti i tavoli "LIBERO",
         magari schedulato, a fine turno lavorativo.
     */
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

    // TODO: data ordine. (cronjob) eliminare ordini del giorno precedente

    // TODO: Soft-delete dei tavoli

    // TODO: Logger

}
