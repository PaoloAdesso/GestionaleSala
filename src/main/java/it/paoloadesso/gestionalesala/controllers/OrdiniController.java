package it.paoloadesso.gestionalesala.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.services.OrdiniService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ordini")
@Validated
@Tag(name = "Gestione Ordini", description = "API per la chiusura e consultazione storico ordini")
public class OrdiniController {

    private static final Logger log = LoggerFactory.getLogger(OrdiniController.class);

    private final OrdiniService ordiniService;

    public OrdiniController(OrdiniService ordiniService) {
        this.ordiniService = ordiniService;
    }

    @Operation(
            summary = "Crea un nuovo ordine",
            description = "Permette al personale di sala di creare un nuovo ordine per un tavolo specifico. " +
                    "L'ordine viene creato con stato IN_ATTESA e il tavolo passa automaticamente a OCCUPATO. " +
                    "La data dell'ordine corrisponde al turno lavorativo corrente: se la richiesta viene " +
                    "effettuata dopo la mezzanotte ma prima dell'orario di chiusura turno (configurabile, " +
                    "default 06:00), l'ordine viene associato al giorno precedente, riflettendo la continuità " +
                    "del servizio oltre la mezzanotte."
    )
    @PostMapping
    public ResponseEntity<OrdiniDTO> creaOrdine(@RequestBody @Valid CreaOrdiniDTO ordine) {
        OrdiniDTO nuovoOrdine = ordiniService.creaOrdine(ordine);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuovoOrdine.getIdOrdine())
                .toUri();

        return ResponseEntity.created(location).body(nuovoOrdine);
    }

    @Operation(
            summary = "Recupera tutti gli ordini aperti",
            description = "Restituisce la lista completa di tutti gli ordini con stato IN_ATTESA, IN_PREPARAZIONE o SERVITO, " +
                    "indipendentemente dalla data di creazione."
    )
    @GetMapping
    public ResponseEntity<List<OrdiniDTO>> getListaTuttiOrdiniAperti() {
        return ResponseEntity.ok(ordiniService.getListaTuttiOrdiniAperti());
    }

    @Operation(
            summary = "Recupera gli ordini del turno lavorativo corrente",
            description = "Restituisce gli ordini del turno lavorativo corrente con stato IN_ATTESA, IN_PREPARAZIONE o SERVITO. " +
                    "Il turno lavorativo considera la continuità del servizio oltre la mezzanotte: " +
                    "se richiesto dopo mezzanotte ma prima delle ore 06:00 (configurabile), vengono restituiti " +
                    "gli ordini del giorno precedente. Utile per il personale di sala durante tutto il turno."
    )
    @GetMapping("/oggi")
    public ResponseEntity<List<OrdiniDTO>> getOrdiniDiOggi() {
        return ResponseEntity.ok(ordiniService.getOrdiniDiOggi());
    }

    @Operation(
            summary = "Recupera tutti gli ordini aperti per tavolo",
            description = "Restituisce tutti gli ordini aperti (IN_ATTESA, IN_PREPARAZIONE, SERVITO) " +
                    "associati a un tavolo specifico, indipendentemente dalla data."
    )
    @GetMapping("/tavolo/{idTavolo}")
    public ResponseEntity<List<OrdiniDTO>> getListaOrdiniApertiPerTavolo(@PathVariable @Positive Long idTavolo) {
        List<OrdiniDTO> listaOrdini = ordiniService.getListaOrdiniApertiByTavolo(idTavolo);
        return ResponseEntity.ok(listaOrdini);
    }

    @Operation(
            summary = "Recupera gli ordini del turno corrente per tavolo",
            description = "Restituisce gli ordini del turno lavorativo corrente con stato IN_ATTESA, IN_PREPARAZIONE o SERVITO " +
                    "per un tavolo specifico. Rispetta la logica del turno lavorativo: se richiesto dopo mezzanotte " +
                    "ma prima dell'orario di chiusura turno, vengono restituiti gli ordini del giorno precedente. " +
                    "Utile per vedere cosa ha ordinato un tavolo durante il turno in corso."
    )
    @GetMapping("/tavolo/{idTavolo}/oggi")
    public ResponseEntity<List<OrdiniDTO>> getOrdiniDiOggiPerTavolo(
            @PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getOrdiniOggiByTavolo(idTavolo));
    }

    @Operation(
            summary = "Recupera i dettagli completi degli ordini per tavolo",
            description = "Restituisce tutti gli ordini aperti di un tavolo con il dettaglio completo di ogni prodotto ordinato: " +
                    "nome, categoria, prezzo, quantità e stato di pagamento. " +
                    "Include ordini di tutte le date ancora non chiusi."
    )
    @GetMapping("/tavolo/{idTavolo}/dettagli")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDTO>> getDettagliOrdiniPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineByIdTavolo(idTavolo));
    }

    @Operation(
            summary = "Recupera i dettagli completi degli ordini del turno corrente per tavolo",
            description = "Restituisce gli ordini del turno lavorativo corrente di un tavolo con il dettaglio completo " +
                    "di ogni prodotto: nome, categoria, prezzo, quantità e stato di pagamento. " +
                    "Rispetta la logica del turno lavorativo oltre la mezzanotte. " +
                    "Utile per preparare il conto o verificare cosa ha consumato il tavolo durante il turno."
    )
    @GetMapping("/tavolo/{idTavolo}/dettagli/oggi")
    public ResponseEntity<List<ListaOrdiniEProdottiByTavoloResponseDTO>> getDettagliOrdiniOggiPerTavolo
            (@PathVariable @Positive Long idTavolo) {
        return ResponseEntity.ok(ordiniService.getDettaglioOrdineDiOggiByIdTavolo(idTavolo));
    }

    @Operation(
            summary = "Recupera tutti gli ordini chiusi",
            description = "Restituisce la lista completa di tutti gli ordini con stato CHIUSO, " +
                    "indipendentemente dalla data. Include il dettaglio dei tavoli e dei prodotti ordinati. " +
                    "Utile per consultare lo storico completo delle vendite."
    )
    @GetMapping("/chiusi")
    public ResponseEntity<List<TavoloConOrdiniChiusiDTO>> getOrdiniChiusi() {
        return ResponseEntity.ok(ordiniService.getOrdiniChiusi());
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
        return ResponseEntity.ok(ordiniService.getOrdiniChiusiDiOggiLavorativo());
    }

    @Operation(
            summary = "Recupera tutti gli ordini eliminati",
            description = "Restituisce la lista completa di tutti gli ordini cancellati (soft delete). " +
                    "Utile per operazioni di audit, controllo o eventuale ripristino. " +
                    "Gli ordini eliminati non sono visibili nelle normali operazioni di gestione."
    )
    @GetMapping("/eliminati")
    public ResponseEntity<List<OrdiniDTO>> getAllOrdiniEliminati() {
        return ResponseEntity.ok(ordiniService.getAllOrdiniEliminati());
    }

    @Operation(
            summary = "Modifica un ordine esistente",
            description = "Permette di modificare un ordine aggiungendo/rimuovendo prodotti o cambiando tavolo. " +
                    "Tutti i campi nel request body sono opzionali: invia solo quelli che vuoi modificare. " +
                    "Non è possibile modificare ordini con stato CHIUSO. " +
                    "L'operazione può completarsi parzialmente: alcuni prodotti possono essere aggiunti/rimossi " +
                    "mentre altri possono fallire (es. prodotto non trovato). " +
                    "La risposta contiene il riepilogo di successi e fallimenti."
    )
    @PatchMapping("/{idOrdine}")
    public ResponseEntity<RisultatoModificaOrdineDTO> modificaOrdine(
            @PathVariable Long idOrdine,
            @Valid @RequestBody ModificaOrdineRequestDTO requestDto) {

        RisultatoModificaOrdineDTO risultato = ordiniService.modificaOrdine(idOrdine, requestDto);

        if (risultato.isOperazioneCompleta()) {
            return ResponseEntity.ok(risultato);
        } else if (risultato.getProdottiAggiunti() > 0) {
            return ResponseEntity.status(207).body(risultato); // 207 - Successo parziale
        } else {
            return ResponseEntity.badRequest().body(risultato); // 400 - Tutti i prodotti falliti
        }
    }

    @Operation(
            summary = "Modifica stato operativo di un ordine",
            description = "Permette di modificare lo stato di un ordine tra IN_ATTESA, IN_PREPARAZIONE e SERVITO. " +
                    "Può modificare anche ordini chiusi (riaprendoli). " +
                    "Non può impostare lo stato CHIUSO (usa l'endpoint dedicato). " +
                    "Utile per il workflow operativo in cucina e sala. " +
                    "Include note opzionali per tracciare il motivo della modifica."
    )
    @PatchMapping("/modifica-stato/{idOrdine}")
    public ResponseEntity<RisultatoModificaStatoOrdineDTO> modificaStatoOrdine(
            @PathVariable @Positive Long idOrdine,
            @RequestBody @Valid ModificaStatoOrdineRequestDTO request) {

        RisultatoModificaStatoOrdineDTO risultato = ordiniService.modificaStatoOrdine(idOrdine, request);
        return ResponseEntity.ok(risultato);
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
        return ResponseEntity.ok(ordiniService.chiudiOrdine(idOrdine));
    }

    @Operation(
            summary = "Ripristina un ordine eliminato",
            description = "Ripristina un ordine precedentemente eliminato (soft delete) rendendolo " +
                    "nuovamente visibile e operativo. Se l'ordine non era CHIUSO al momento della " +
                    "cancellazione, il tavolo collegato viene automaticamente impostato come OCCUPATO. " +
                    "Operazione utile per correggere cancellazioni accidentali."
    )
    @PatchMapping("/{ordineId}/ripristina")
    public ResponseEntity<OrdiniDTO> ripristinaSingoloProdotto(@PathVariable @Positive Long ordineId) {
        return ResponseEntity.ok(ordiniService.ripristinaOrdine(ordineId));
    }

    @Operation(
            summary = "Elimina un ordine (soft delete)",
            description = "Elimina logicamente un ordine impostando il flag deleted = true. " +
                    "L'ordine rimane nel database per audit trail ma non è più visibile " +
                    "nelle operazioni normali. Operazione irreversibile tramite API standard."
    )
    @DeleteMapping("/{idOrdine}")
    public ResponseEntity<Void> deleteOrdine(@PathVariable @Positive Long idOrdine){
        ordiniService.eliminaOrdine(idOrdine);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Elimina un ordine",
            description = "Elimina definitivamente un ordine ed i relativi prodotti ordinati collegati." +
                    " Operazione irreversibile."    )
    @DeleteMapping("/hard-delete/{idOrdine}")
    public ResponseEntity<Void> hardDeleteOrdine(@PathVariable @Positive Long idOrdine){
        ordiniService.eliminaFisicamenteOrdine(idOrdine);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Elimina tutti gli ordini più vecchi di una settimana",
            description = "Elimina definitivamente gli ordini ed i relativi prodotti ordinati collegati, più vecchi di una settimana." +
                    " Operazione irreversibile."    )
    @DeleteMapping("/hard-delete-ordini-settimana-precedente")
    public ResponseEntity<Void> hardDeleteOrdiniSettimanaPrecedente(){
        ordiniService.eliminaOrdiniSettimanaPrecedente();
        log.info("Effettuata eliminazione manuale degli ordini della settimana precedente");
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Elimina tutti gli ordini del giorno precedente (soft-delete)",
            description = "Elimina gli ordini ed i relativi prodotti ordinati collegati del giorno precedente")
    @DeleteMapping("/soft-delete-ordini-giorno-precedente")
    public ResponseEntity<Void> hardDeleteOrdiniVecchi(){
        ordiniService.eliminaOrdiniGiornoPrecedente();
        log.info("Effettuata eliminazione manuale degli ordini di ieri (soft-delete)");
        return ResponseEntity.noContent().build();
    }
}
