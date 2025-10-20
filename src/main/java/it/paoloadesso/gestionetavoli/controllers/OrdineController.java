package it.paoloadesso.gestionetavoli.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionetavoli.dto.*;
import it.paoloadesso.gestionetavoli.services.OrdiniService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
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
public class OrdineController {

    private final OrdiniService ordiniService;

    public OrdineController(OrdiniService ordiniService) {
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
            summary = "Modifica un ordine esistente",
            description = "Permette di modificare un ordine aggiungendo/rimuovendo prodotti o cambiando tavolo. " +
                    "Tutti i campi nel request body sono opzionali: invia solo quelli che vuoi modificare. " +
                    "Non è possibile modificare ordini con stato CHIUSO. " +
                    "L'operazione può completarsi parzialmente: alcuni prodotti possono essere aggiunti/rimossi " +
                    "mentre altri possono fallire (es. prodotto non trovato). " +
                    "La risposta contiene il riepilogo di successi e fallimenti."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<RisultatoModificaOrdineDTO> modificaOrdine(
            @PathVariable Long id,
            @Valid @RequestBody ModificaOrdineRequestDTO requestDto) {

        RisultatoModificaOrdineDTO risultato = ordiniService.modificaOrdine(id, requestDto);

        if (risultato.isOperazioneCompleta()) {
            return ResponseEntity.ok(risultato);
        } else if (risultato.getProdottiAggiunti() > 0) {
            return ResponseEntity.status(207).body(risultato); // 207 - Successo parziale
        } else {
            return ResponseEntity.badRequest().body(risultato); // 400 - Tutti i prodotti falliti
        }
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
        return ResponseEntity.ok(ordiniService.getOrdiniChiusiDiOggi());
    }

}
