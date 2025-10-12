package it.paoloadesso.gestionetavoli.services;

import it.paoloadesso.gestionetavoli.dto.AnnullaPagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.dto.PagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import it.paoloadesso.gestionetavoli.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionetavoli.repositories.OrdiniRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final OrdiniRepository ordiniRepository;
    private final OrdineService ordineService;


    public PagamentoService(OrdiniProdottiRepository ordiniProdottiRepository, OrdiniRepository ordiniRepository, OrdineService ordineService) {
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniRepository = ordiniRepository;
        this.ordineService = ordineService;
    }


    public void pagaProdottoInOrdine(Long idOrdine, Long idProdotto) {
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PRODOTTO_NON_TROVATO_IN_ORDINE: Prodotto con ID " + idProdotto +
                                " non trovato nell'ordine " + idOrdine
                ));

        if (prodottoOrdine.getStatoPagato() == StatoPagato.PAGATO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "PRODOTTO_GIA_PAGATO: Il prodotto è già stato pagato"
            );
        }

        prodottoOrdine.setStatoPagato(StatoPagato.PAGATO);

        ordiniProdottiRepository.save(prodottoOrdine);
    }

    @Transactional
    public PagamentoRisultatoDto pagaTuttoEChiudiSeRichiesto(Long idOrdine, boolean chiudiOrdine) {
        // Prima paga
        PagamentoRisultatoDto risultato = pagaTuttoOrdine(idOrdine);

        // Se richiesto, chiudi ordine
        if (chiudiOrdine) {
            ordineService.chiudiOrdine(idOrdine);
            risultato.setStatoOrdine(StatoOrdine.CHIUSO);
        }

        return risultato;
    }



    @Transactional
    public PagamentoRisultatoDto pagaTuttoOrdine(Long idOrdine) {

        // Verifico ordine
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ORDINE_NON_TROVATO: Ordine con ID " + idOrdine + " non trovato"
            );
        }

        // Trovo prodotti non pagati
        List<OrdiniProdottiEntity> prodottiNonPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.NON_PAGATO);

        if (prodottiNonPagati.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "NESSUN_PRODOTTO_DA_PAGARE: Nessun prodotto da pagare per l'ordine " + idOrdine
            );
        }

        // Pago tutto e salva
        prodottiNonPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.PAGATO);
        });

        ordiniProdottiRepository.saveAll(prodottiNonPagati);

        // Conto il totale dei pezzi pagati
        int totalePezziPagati = prodottiNonPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        // Risultato
        return new PagamentoRisultatoDto(
                idOrdine,
                prodottiNonPagati.size(),
                totalePezziPagati,
                calcolaTotale(prodottiNonPagati),
                LocalDateTime.now(),
                ordine.getStatoOrdine()
        );
    }

    private BigDecimal calcolaTotale(List<OrdiniProdottiEntity> prodottiNonPagati) {
        BigDecimal totale = BigDecimal.ZERO;

        for (OrdiniProdottiEntity prodottoOrdine : prodottiNonPagati) {
            BigDecimal prezzoProdotto = prodottoOrdine.getProdotto().getPrezzo();

            Integer quantita = prodottoOrdine.getQuantitaProdotto();

            BigDecimal subtotale = prezzoProdotto.multiply(BigDecimal.valueOf(quantita));

            totale = totale.add(subtotale);
        }

        return totale;
    }

    public void annullaPagamentoProdottoInOrdine(Long idOrdine, Long idProdotto) {
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PRODOTTO_NON_TROVATO_IN_ORDINE: Prodotto con ID " + idProdotto +
                                " non trovato nell'ordine " + idOrdine
                ));

        if (prodottoOrdine.getStatoPagato() == StatoPagato.NON_PAGATO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PRODOTTO_NON_PAGATO: Il prodotto non è stato ancora pagato"
            );
        }

        prodottoOrdine.setStatoPagato(StatoPagato.NON_PAGATO);

        ordiniProdottiRepository.save(prodottoOrdine);
    }

    @Transactional
    public AnnullaPagamentoRisultatoDto annullaPagamentoTuttoOrdine(Long idOrdine) {

        // Verifica ordine
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ORDINE_NON_TROVATO: Ordine con ID " + idOrdine + " non trovato"
            );
        }

        // Trova prodotti pagati
        List<OrdiniProdottiEntity> prodottiPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.PAGATO);

        if (prodottiPagati.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "NESSUN_PRODOTTO_PAGATO: Nessun prodotto pagato da annullare per questo ordine con ID " + idOrdine
            );
        }

        // Annulla tutti i pagamenti e salva
        prodottiPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.NON_PAGATO);
        });

        ordiniProdottiRepository.saveAll(prodottiPagati);

        // Conto il totale dei pezzi pagati
        int totalePezziNonPagati = prodottiPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        // Risultato
        return new AnnullaPagamentoRisultatoDto(
                idOrdine,
                prodottiPagati.size(),
                totalePezziNonPagati,
                calcolaTotale(prodottiPagati),
                LocalDateTime.now()
        );
    }

}
