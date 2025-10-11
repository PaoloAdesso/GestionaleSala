package it.paoloadesso.gestionetavoli.controllers;

import it.paoloadesso.gestionetavoli.dto.AnnullaPagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.dto.PagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.services.PagamentoService;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagamenti")
@Validated
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/ordini/{idOrdine}/prodotti/{idProdotto}")
    public ResponseEntity<String> pagaProdotto(
            @PathVariable @Positive Long idOrdine,
            @PathVariable @Positive Long idProdotto) {

        pagamentoService.pagaProdottoInOrdine(idOrdine, idProdotto);
        return ResponseEntity.ok("Prodotto pagato con successo");
    }

    @PostMapping("/ordini/{idOrdine}/prodotti/{idProdotto}/annulla")
    public ResponseEntity<String> annullaPagamentoProdotto(
            @PathVariable @Positive Long idOrdine,
            @PathVariable @Positive Long idProdotto) {

        pagamentoService.annullaPagamentoProdottoInOrdine(idOrdine, idProdotto);
        return ResponseEntity.ok("Pagamento prodotto annullato con successo");
    }

    @PostMapping("/ordini/{idOrdine}")
    public ResponseEntity<PagamentoRisultatoDto> pagaTutto(
            @PathVariable @Positive Long idOrdine) {

        PagamentoRisultatoDto risultato = pagamentoService.pagaTuttoOrdine(idOrdine);
        return ResponseEntity.ok(risultato);
    }

    @PostMapping("/ordini/{idOrdine}/annulla")
    public ResponseEntity<AnnullaPagamentoRisultatoDto> annullaTutto(
            @PathVariable @Positive Long idOrdine) {

        AnnullaPagamentoRisultatoDto risultato = pagamentoService.annullaPagamentoTuttoOrdine(idOrdine);
        return ResponseEntity.ok(risultato);
    }
}
