package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.AnnullaPagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.dto.PagamentoRisultatoDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import it.paoloadesso.gestionalesala.enums.StatoPagato;
import it.paoloadesso.gestionalesala.exceptionhandling.EntitaNonTrovataException;
import it.paoloadesso.gestionalesala.exceptionhandling.StatoNonValidoException;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentiService {

    private static final Logger log = LoggerFactory.getLogger(PagamentiService.class);

    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final OrdiniRepository ordiniRepository;
    private final OrdiniService ordiniService;

    public PagamentiService(OrdiniProdottiRepository ordiniProdottiRepository,
                            OrdiniRepository ordiniRepository,
                            OrdiniService ordiniService) {
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniRepository = ordiniRepository;
        this.ordiniService = ordiniService;
    }

    public void pagaProdottoInOrdine(Long idOrdine, Long idProdotto) {
        log.info("Pagamento singolo prodotto richiesto - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);

        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        // Cerco il prodotto nell'ordine, se non lo trovo lancio un errore
        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> {
                    log.error("Prodotto {} non trovato nell'ordine {}", idProdotto, idOrdine);
                    return new EntitaNonTrovataException("Prodotto con ID " + idProdotto + " non trovato nell'ordine " + idOrdine);
                });

        // Controllo che il prodotto non sia già stato pagato
        if (prodottoOrdine.getStatoPagato() == StatoPagato.PAGATO) {
            log.warn("Tentativo di pagare un prodotto già pagato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
            throw new StatoNonValidoException("pagare il prodotto", "già pagato");
        }

        prodottoOrdine.setStatoPagato(StatoPagato.PAGATO);
        ordiniProdottiRepository.save(prodottoOrdine);

        log.info("Prodotto pagato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
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
        log.info("Pagamento totale ordine richiesto - Ordine: {}, chiudiOrdine: {}", idOrdine, chiudiOrdine);

        PagamentoRisultatoDTO risultato = pagaTuttoOrdine(idOrdine);

        // Se l'utente ha richiesto di chiudere l'ordine, lo chiudo
        if (chiudiOrdine) {
            ordiniService.chiudiOrdine(idOrdine);
            risultato.setStatoOrdine(StatoOrdine.CHIUSO);
            log.info("Ordine {} chiuso dopo pagamento totale", idOrdine);
        }

        return risultato;
    }

    @Transactional
    public PagamentoRisultatoDTO pagaTuttoOrdine(Long idOrdine) {
        log.info("Pagamento totale ordine - Ordine: {}", idOrdine);

        // Prima verifico che l'ordine esista
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            log.error("Ordine non trovato per pagamento totale - ID: {}", idOrdine);
            throw new EntitaNonTrovataException("Ordine", idOrdine);
        }

        // Trovo tutti i prodotti di questo ordine che NON sono ancora stati pagati
        List<OrdiniProdottiEntity> prodottiNonPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.NON_PAGATO);

        // Se non ci sono prodotti da pagare, lancio un errore
        if (prodottiNonPagati.isEmpty()) {
            log.warn("Nessun prodotto da pagare per l'ordine {}", idOrdine);
            throw new StatoNonValidoException("pagare l'ordine " + idOrdine, "nessun prodotto da pagare");
        }

        prodottiNonPagati.forEach(prodotto -> prodotto.setStatoPagato(StatoPagato.PAGATO));
        ordiniProdottiRepository.saveAll(prodottiNonPagati);

        // Calcolo il totale dei pezzi pagati (sommo tutte le quantità)
        int totalePezziPagati = prodottiNonPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        PagamentoRisultatoDTO dto = new PagamentoRisultatoDTO(
                idOrdine,
                prodottiNonPagati.size(),
                totalePezziPagati,
                calcolaTotale(prodottiNonPagati),
                LocalDateTime.now(),
                ordine.getStatoOrdine()
        );

        log.info("Pagamento totale completato - Ordine: {}, Tipi: {}, Pezzi: {}", idOrdine, prodottiNonPagati.size(), totalePezziPagati);
        return dto;
    }

    private BigDecimal calcolaTotale(List<OrdiniProdottiEntity> prodotti) {
        BigDecimal totale = BigDecimal.ZERO;
        for (OrdiniProdottiEntity prodottoOrdine : prodotti) {
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
        log.info("Annullamento pagamento singolo prodotto - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);

        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, idProdotto);

        // Cerco il prodotto nell'ordine
        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> {
                    log.error("Prodotto {} non trovato nell'ordine {}", idProdotto, idOrdine);
                    return new EntitaNonTrovataException("Prodotto con ID " + idProdotto + " non trovato nell'ordine " + idOrdine);
                });

        // Controllo che il prodotto sia stato effettivamente pagato
        if (prodottoOrdine.getStatoPagato() == StatoPagato.NON_PAGATO) {
            log.warn("Tentativo di annullare pagamento su prodotto non pagato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
            throw new StatoNonValidoException("annullare il pagamento", "prodotto non ancora pagato");
        }

        // Riporto il prodotto allo stato "non pagato" e salvo
        prodottoOrdine.setStatoPagato(StatoPagato.NON_PAGATO);
        ordiniProdottiRepository.save(prodottoOrdine);

        log.info("Pagamento annullato - Ordine: {}, Prodotto: {}", idOrdine, idProdotto);
    }

    @Transactional
    public AnnullaPagamentoRisultatoDTO annullaPagamentoTuttoOrdine(Long idOrdine) {
        log.info("Annullamento pagamento totale ordine - Ordine: {}", idOrdine);

        // Verifico che l'ordine esista
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            log.error("Ordine non trovato per annullamento pagamento - ID: {}", idOrdine);
            throw new EntitaNonTrovataException("Ordine", idOrdine);
        }

        // Trovo tutti i prodotti che erano stati pagati
        List<OrdiniProdottiEntity> prodottiPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.PAGATO);

        // Se non ci sono prodotti pagati da annullare, lancio un errore
        if (prodottiPagati.isEmpty()) {
            log.warn("Nessun prodotto pagato da annullare per l'ordine {}", idOrdine);
            // HttpStatus.UNPROCESSABLE_ENTITY 422 nel GlobalExceptionHandler
            throw new StatoNonValidoException("annullare il pagamento totale", "nessun prodotto risulta pagato");
        }

        prodottiPagati.forEach(prodotto -> prodotto.setStatoPagato(StatoPagato.NON_PAGATO));
        ordiniProdottiRepository.saveAll(prodottiPagati);

        // Calcolo quanti pezzi ho rimesso come "non pagati"
        int totalePezziNonPagati = prodottiPagati.stream()
                .mapToInt(OrdiniProdottiEntity::getQuantitaProdotto)
                .sum();

        AnnullaPagamentoRisultatoDTO dto = new AnnullaPagamentoRisultatoDTO(
                idOrdine,
                prodottiPagati.size(),
                totalePezziNonPagati,
                calcolaTotale(prodottiPagati),
                LocalDateTime.now()
        );

        log.info("Annullamento pagamento totale completato - Ordine: {}, Tipi: {}, Pezzi: {}", idOrdine, prodottiPagati.size(), totalePezziNonPagati);
        return dto;
    }
}
