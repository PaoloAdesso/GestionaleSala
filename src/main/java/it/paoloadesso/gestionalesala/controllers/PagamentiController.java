package it.paoloadesso.gestionalesala.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.paoloadesso.gestionalesala.dto.AnnullaPagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.dto.PagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.services.PagamentiService;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamenti")
@Validated
@Tag(name = "Gestione Pagamenti", description = "API per gestire i pagamenti di prodotti e ordini completi")
public class PagamentiController {

    private static final Logger log = LoggerFactory.getLogger(PagamentiController.class);

    private final PagamentiService pagamentiService;

    public PagamentiController(PagamentiService pagamentiService) {
        this.pagamentiService = pagamentiService;
    }

    @Operation(
            summary = "Segna un prodotto come pagato",
            description = "Imposta lo stato di pagamento di uno specifico prodotto in un ordine a PAGATO. " +
                    "Utile quando i clienti pagano singolarmente (conti separati). " +
                    "Il prodotto deve essere presente nell'ordine specificato."
    )
    @PostMapping("/ordini/{idOrdine}/prodotti/{idProdotto}")
    public ResponseEntity<String> pagaProdotto(
            @PathVariable @Positive Long idOrdine,
            @PathVariable @Positive Long idProdotto) {

        log.info("Richiesta pagamento singolo - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
        pagamentiService.pagaProdottoInOrdine(idOrdine, idProdotto);
        log.info("Pagamento completato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
        return ResponseEntity.ok("Prodotto pagato con successo");
    }

    @Operation(
            summary = "Annulla pagamento di un prodotto",
            description = "Reimposta lo stato di pagamento di un prodotto a NON_PAGATO. " +
                    "Utile per correggere errori nel segno pagamento o in caso di rimborso. " +
                    "L'ordine deve esistere e il prodotto deve essere presente in esso."
    )
    @PostMapping("/ordini/{idOrdine}/prodotti/{idProdotto}/annulla")
    public ResponseEntity<String> annullaPagamentoProdotto(
            @PathVariable @Positive Long idOrdine,
            @PathVariable @Positive Long idProdotto) {

        log.info("Richiesta annullamento pagamento - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
        pagamentiService.annullaPagamentoProdottoInOrdine(idOrdine, idProdotto);
        log.info("Annullamento pagamento completato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
        return ResponseEntity.ok("Pagamento prodotto annullato con successo");
    }

    @Operation(
            summary = "Segna tutti i prodotti dell'ordine come pagati",
            description = "Imposta lo stato PAGATO a tutti i prodotti dell'ordine. " +
                    "Se il parametro chiudiOrdine è true, l'ordine viene anche chiuso automaticamente. " +
                    "La risposta include il riepilogo: numero prodotti pagati, importo totale, " +
                    "e se l'ordine è stato chiuso. Operazione tipica alla cassa per chiusura conto completo."
    )
    @PostMapping("/ordini/{idOrdine}")
    public ResponseEntity<PagamentoRisultatoDTO> pagaTutto(
            @PathVariable @Positive Long idOrdine,
            @Parameter(description = "Se true, chiude anche l'ordine dopo aver segnato tutto come pagato",
                    example = "true")
            @RequestParam(defaultValue = "false") boolean chiudiOrdine) {

        log.info("Richiesta pagamento totale - Ordine: {}, chiudiOrdine: {}", idOrdine, chiudiOrdine);
        PagamentoRisultatoDTO risultato = pagamentiService.pagaTuttoEChiudiSeRichiesto(idOrdine, chiudiOrdine);
        log.info("Pagamento totale completato - Ordine: {}, Importo: €{}, Chiuso: {}",
                idOrdine, risultato.getTotalePagato(), chiudiOrdine);
        return ResponseEntity.ok(risultato);
    }

    @Operation(
            summary = "Annulla tutti i pagamenti di un ordine",
            description = "Reimposta lo stato di tutti i prodotti dell'ordine a NON_PAGATO. " +
                    "Se l'ordine era chiuso, viene riaperto (stato IN_ATTESA). " +
                    "La risposta include il numero di prodotti reimpostati e il nuovo stato dell'ordine. " +
                    "Utile per correzioni massive o gestione rimborsi completi."
    )
    @PostMapping("/ordini/{idOrdine}/annulla")
    public ResponseEntity<AnnullaPagamentoRisultatoDTO> annullaTutto(
            @PathVariable @Positive Long idOrdine) {

        log.info("Richiesta annullamento pagamenti totali - Ordine: {}", idOrdine);
        AnnullaPagamentoRisultatoDTO risultato = pagamentiService.annullaPagamentoTuttoOrdine(idOrdine);
        log.info("Annullamento pagamenti totali completato - Ordine: {}, Tipi prodotto: {}, Pezzi: {}, Importo: €{}",
                idOrdine, risultato.getQuantitaProdottiNonPagati(), risultato.getTotalePezziNonPagati(),
                risultato.getTotalePagamentiAnnullati());
        return ResponseEntity.ok(risultato);
    }
}
