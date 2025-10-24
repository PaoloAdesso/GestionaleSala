package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.AnnullaPagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.dto.PagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import it.paoloadesso.gestionalesala.enums.StatoPagato;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentiService {

    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final OrdiniRepository ordiniRepository;
    private final OrdiniService ordiniService;


    public PagamentiService(OrdiniProdottiRepository ordiniProdottiRepository, OrdiniRepository ordiniRepository, OrdiniService ordiniService) {
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniRepository = ordiniRepository;
        this.ordiniService = ordiniService;
    }


    public void pagaProdottoInOrdine(Long idOrdine, Long idProdotto) {
        // Creo la chiave composita per trovare il prodotto in questo ordine specifico
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        // Cerco il prodotto nell'ordine, se non lo trovo lancio un errore
        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PRODOTTO_NON_TROVATO_IN_ORDINE: Prodotto con ID " + idProdotto +
                                " non trovato nell'ordine " + idOrdine
                ));

        // Controllo che il prodotto non sia già stato pagato
        if (prodottoOrdine.getStatoPagato() == StatoPagato.PAGATO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "PRODOTTO_GIA_PAGATO: Il prodotto è già stato pagato"
            );
        }
        // Imposto il prodotto come pagato e salvo
        prodottoOrdine.setStatoPagato(StatoPagato.PAGATO);
        ordiniProdottiRepository.save(prodottoOrdine);
    }

    /**
     * Questo metodo paga tutti i prodotti di un ordine e opzionalmente chiude l'ordine.
     * Uso @Transactional perché faccio due operazioni che devono avere successo entrambe:
     * 1. Pago tutti i prodotti
     * 2. (Opzionale) Chiudo l'ordine
     * Se una fallisce, annullo anche l'altra.
     */
    @Transactional
    public PagamentoRisultatoDTO pagaTuttoEChiudiSeRichiesto(Long idOrdine, boolean chiudiOrdine) {
        // Prima pago tutti i prodotti dell'ordine
        PagamentoRisultatoDTO risultato = pagaTuttoOrdine(idOrdine);

        // Se l'utente ha richiesto di chiudere l'ordine, lo chiudo
        if (chiudiOrdine) {
            ordiniService.chiudiOrdine(idOrdine);
            risultato.setStatoOrdine(StatoOrdine.CHIUSO);
        }

        return risultato;
    }

    @Transactional
    public PagamentoRisultatoDTO pagaTuttoOrdine(Long idOrdine) {

        // Prima verifico che l'ordine esista
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ORDINE_NON_TROVATO: Ordine con ID " + idOrdine + " non trovato"
            );
        }

        // Trovo tutti i prodotti di questo ordine che NON sono ancora stati pagati
        List<OrdiniProdottiEntity> prodottiNonPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.NON_PAGATO);

        // Se non ci sono prodotti da pagare, lancio un errore
        if (prodottiNonPagati.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "NESSUN_PRODOTTO_DA_PAGARE: Nessun prodotto da pagare per l'ordine " + idOrdine
            );
        }

        // Imposto tutti i prodotti come pagati
        prodottiNonPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.PAGATO);
        });

        // Salvo tutte le modifiche in una volta sola
        ordiniProdottiRepository.saveAll(prodottiNonPagati);

        // Calcolo il totale dei pezzi pagati (sommo tutte le quantità)
        int totalePezziPagati = prodottiNonPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        // Creo e restituisco il risultato con tutte le info del pagamento
        return new PagamentoRisultatoDTO(
                idOrdine,
                prodottiNonPagati.size(),            // Quanti tipi di prodotto diversi ho pagato
                totalePezziPagati,                   // Quanti pezzi in totale ho pagato
                calcolaTotale(prodottiNonPagati),    // Quanto ho speso in totale
                LocalDateTime.now(),                 // Quando è avvenuto il pagamento
                ordine.getStatoOrdine()              // Lo stato attuale dell'ordine
        );
    }

    /**
     * Metodo d'aiuto: calcola il totale da pagare moltiplicando prezzo × quantità per ogni prodotto
     * e sommando tutti i subtotali.
     */
    private BigDecimal calcolaTotale(List<OrdiniProdottiEntity> prodottiNonPagati) {
        BigDecimal totale = BigDecimal.ZERO;

        for (OrdiniProdottiEntity prodottoOrdine : prodottiNonPagati) {
            // Prendo il prezzo del prodotto
            BigDecimal prezzoProdotto = prodottoOrdine.getProdotto().getPrezzo();

            // Prendo la quantità ordinata
            Integer quantita = prodottoOrdine.getQuantitaProdotto();

            // Calcolo il subtotale (prezzo × quantità)
            BigDecimal subtotale = prezzoProdotto.multiply(BigDecimal.valueOf(quantita));

            // Aggiungo al totale
            totale = totale.add(subtotale);
        }

        return totale;
    }

    /**
     * Questo metodo annulla il pagamento di un singolo prodotto.
     * Utile se ho marcato come pagato per errore e devo correggere.
     * Riporto il prodotto allo stato "non pagato".
     */
    public void annullaPagamentoProdottoInOrdine(Long idOrdine, Long idProdotto) {
        // Creo la chiave composita per trovare il prodotto in questo ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        // Cerco il prodotto nell'ordine
        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "PRODOTTO_NON_TROVATO_IN_ORDINE: Prodotto con ID " + idProdotto +
                                " non trovato nell'ordine " + idOrdine
                ));

        // Controllo che il prodotto sia stato effettivamente pagato
        if (prodottoOrdine.getStatoPagato() == StatoPagato.NON_PAGATO) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PRODOTTO_NON_PAGATO: Il prodotto non è stato ancora pagato"
            );
        }

        // Riporto il prodotto allo stato "non pagato" e salvo
        prodottoOrdine.setStatoPagato(StatoPagato.NON_PAGATO);
        ordiniProdottiRepository.save(prodottoOrdine);
    }

    @Transactional
    public AnnullaPagamentoRisultatoDTO annullaPagamentoTuttoOrdine(Long idOrdine) {

        // Verifico che l'ordine esista
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ORDINE_NON_TROVATO: Ordine con ID " + idOrdine + " non trovato"
            );
        }

        // Trovo tutti i prodotti che erano stati pagati
        List<OrdiniProdottiEntity> prodottiPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.PAGATO);

        // Se non ci sono prodotti pagati da annullare, lancio un errore
        if (prodottiPagati.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "NESSUN_PRODOTTO_PAGATO: Nessun prodotto pagato da annullare per questo ordine con ID " + idOrdine
            );
        }

        // Rimetto tutti i prodotti allo stato "non pagato"
        prodottiPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.NON_PAGATO);
        });

        // Salvo tutte le modifiche
        ordiniProdottiRepository.saveAll(prodottiPagati);

        // Calcolo quanti pezzi ho rimesso come "non pagati"
        int totalePezziNonPagati = prodottiPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        // Creo e restituisco il risultato dell'annullamento
        return new AnnullaPagamentoRisultatoDTO(
                idOrdine,
                prodottiPagati.size(),
                totalePezziNonPagati,
                calcolaTotale(prodottiPagati),
                LocalDateTime.now()
        );
    }
}
