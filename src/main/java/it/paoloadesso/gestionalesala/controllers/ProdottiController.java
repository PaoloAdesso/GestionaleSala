package it.paoloadesso.gestionalesala.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionalesala.dto.CreaProdottiDTO;
import it.paoloadesso.gestionalesala.dto.ProdottiConDettaglioDeleteDTO;
import it.paoloadesso.gestionalesala.dto.ProdottiDTO;
import it.paoloadesso.gestionalesala.services.ProdottiService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("prodotti")
@Validated
@Tag(name = "Gestione Prodotti", description = "API per gestire il menu del ristorante: prodotti attivi, eliminati e categorie")
public class ProdottiController {

    private final ProdottiService prodottiService;

    public ProdottiController(ProdottiService prodottiService) {
        this.prodottiService = prodottiService;
    }

    @Operation(
            summary = "Crea un nuovo prodotto",
            description = "Aggiunge un nuovo prodotto al menu del ristorante specificando nome, categoria e prezzo. " +
                    "Il prodotto diventa immediatamente disponibile per essere ordinato. " +
                    "Restituisce il prodotto creato con l'ID generato."
    )
    @PostMapping
    public ResponseEntity<ProdottiDTO> creaProdotto(@RequestBody @Valid CreaProdottiDTO prodotto) {
        ProdottiDTO nuovoProdotto = prodottiService.creaProdotto(prodotto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(nuovoProdotto.getIdProdotto())
                .toUri();

        return ResponseEntity.created(location).body(nuovoProdotto);
    }

    @Operation(
            summary = "Recupera tutti i prodotti attivi",
            description = "Restituisce la lista completa di tutti i prodotti attivi (non eliminati) " +
                    "disponibili nel menu del ristorante con informazioni su nome, categoria e prezzo. " +
                    "Utile per visualizzare il catalogo completo."
    )
    @GetMapping
    public ResponseEntity<List<ProdottiDTO>> getAllProdotti() {
        return ResponseEntity.ok(prodottiService.getAllProdotti());
    }

    @Operation(
            summary = "Recupera tutti i prodotti eliminati",
            description = "Restituisce la lista dei prodotti che sono stati eliminati (soft-delete). " +
                    "Questi prodotti non sono più ordinabili ma rimangono nel database per storico. " +
                    "Utile per consultare prodotti rimossi dal menu o per eventuale ripristino."
    )
    @GetMapping("/eliminati")
    public ResponseEntity<List<ProdottiDTO>> getAllProdottiEliminati() {
        return ResponseEntity.ok(prodottiService.getAllProdottiEliminati());
    }

    @Operation(
            summary = "Recupera tutte le categorie",
            description = "Restituisce la lista di tutte le categorie di prodotti presenti nel menu " +
                    "(solo prodotti attivi). Utile per creare filtri o menu categorizzati nell'interfaccia utente."
    )
    @GetMapping("/categorie")
    public ResponseEntity<List<String>> getAllCategorie() {
        return ResponseEntity.ok(prodottiService.getAllCategorie());
    }

    @Operation(
            summary = "Cerca prodotti per nome",
            description = "Cerca prodotti attivi il cui nome contiene la stringa specificata (ricerca case-insensitive). " +
                    "Utile per il personale quando deve cercare rapidamente un prodotto senza conoscerne il nome esatto. " +
                    "Esempio: 'pizza' trova 'Pizza Margherita', 'Pizza Diavola', ecc."
    )
    @GetMapping("/cerca-per-nome")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerNome(
            @Parameter(description = "Parte del nome del prodotto da cercare (case-insensitive)",
                    example = "pizza")
            @RequestParam @NotBlank String nomeProdotto) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingNome(nomeProdotto));
    }

    @Operation(
            summary = "Cerca in attivi ed eliminati",
            description = "Cerca prodotti sia attivi (ordinabili) che eliminati (soft-delete) filtrando per nome, in modo case-insensitive. Utile per audit e ripristini: consente di vedere prodotti rimossi dal menu e valutarne il recupero."
    )
    @GetMapping("/cerca-per-nome-tutti-i-prodotti-attivi-ed-eliminati")
    public ResponseEntity<List<ProdottiConDettaglioDeleteDTO>> cercaTuttiProdottiAttiviEdEliminatiPerNome(
            @Parameter(description = "Parte del nome del prodotto da cercare (case-insensitive)", example = "pizza")
            @RequestParam @NotBlank String nomeProdotto
    ) {
        return ResponseEntity.ok(prodottiService.getTuttiProdottiAttiviEdEliminatiByContainingNome(nomeProdotto));
    }

    @Operation(
            summary = "Cerca prodotti per categoria",
            description = "Restituisce tutti i prodotti attivi che appartengono alla categoria specificata (case-insensitive). " +
                    "Utile per filtrare il menu per tipo di prodotto. " +
                    "Esempi di categorie: Antipasti, Primi, Secondi, Dolci, Bevande."
    )
    @GetMapping("/cerca/categoria")
    public ResponseEntity<List<ProdottiDTO>> cercaProdottiPerCategoria(
            @Parameter(description = "Nome della categoria da filtrare (case-insensitive)",
                    example = "Primi")
            @RequestParam @NotBlank String nomeCategoria) {
        return ResponseEntity.ok(prodottiService.getProdottiByContainingCategoria(nomeCategoria));
    }

    @Operation(
            summary = "Ripristina un prodotto eliminato",
            description = "Riattiva un prodotto precedentemente eliminato (soft-delete), rendendolo nuovamente " +
                    "disponibile per essere ordinato. Utile per rimettere in menu prodotti stagionali " +
                    "o prodotti rimossi per errore."
    )
    @PatchMapping("/{prodottoId}/ripristina")
    public ResponseEntity<ProdottiConDettaglioDeleteDTO> ripristinaSingoloProdotto(@PathVariable @Positive Long prodottoId) {
        return ResponseEntity.ok(prodottiService.ripristinaSingoloProdotto(prodottoId));
    }

    @Operation(
            summary = "Elimina un prodotto (soft-delete)",
            description = "Elimina logicamente un prodotto dal menu rendendolo non più ordinabile. " +
                    "Il prodotto rimane nel database per mantenere lo storico degli ordini passati (soft-delete). " +
                    "Non viene rimosso fisicamente per preservare l'integrità referenziale con ordini esistenti. " +
                    "Può essere ripristinato successivamente."
    )
    @DeleteMapping("{prodottoId}")
    public ResponseEntity<Void> eliminaSingoloProdotto(@PathVariable @Positive Long prodottoId){
        prodottiService.deleteProdotto(prodottoId);
        return ResponseEntity.noContent().build();
    }
}

