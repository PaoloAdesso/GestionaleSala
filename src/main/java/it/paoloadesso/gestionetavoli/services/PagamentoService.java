package it.paoloadesso.gestionetavoli.services;

import it.paoloadesso.gestionetavoli.dto.AnnullaPagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.dto.PagamentoRisultatoDto;
import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import it.paoloadesso.gestionetavoli.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionetavoli.repositories.OrdiniRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final OrdiniRepository ordiniRepository;

    public PagamentoService(OrdiniProdottiRepository ordiniProdottiRepository, OrdiniRepository ordiniRepository) {
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniRepository = ordiniRepository;
    }


    public void pagaProdottoInOrdine(Long idOrdine, Long idProdotto) {
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine,idProdotto);

        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato nell'ordine."));

        if(prodottoOrdine.getStatoPagato() == StatoPagato.PAGATO){
            throw new RuntimeException("Prodotto già pagato.");
        }

        prodottoOrdine.setStatoPagato(StatoPagato.PAGATO);

        ordiniProdottiRepository.save(prodottoOrdine);
    }

    public boolean isOrdineCompletamentePagato(Long idOrdine) {
        // Conto prodotti totali
        Integer prodottiTotali = ordiniProdottiRepository.countByOrdineIdOrdine(idOrdine);

        // Conto prodotti pagati
        Integer prodottiPagati = ordiniProdottiRepository.countByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.PAGATO);

        if (prodottiTotali == null || prodottiPagati == null) {
            return false;
        }
        return prodottiTotali.equals(prodottiPagati);
    }

    @Transactional
    public PagamentoRisultatoDto pagaTuttoOrdine(Long idOrdine) {

        // Verifica ordine
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new RuntimeException("Ordine inesistente.");
        }

        // Trova prodotti non pagati
        List<OrdiniProdottiEntity> prodottiNonPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.NON_PAGATO);

        if (prodottiNonPagati.isEmpty()) {
            throw new RuntimeException("Nessun prodotto da pagare.");
        }

        // Paga tutto e salva
        prodottiNonPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.PAGATO);
        });

        ordiniProdottiRepository.saveAll(prodottiNonPagati);

        // Risultato
        return new PagamentoRisultatoDto(
                idOrdine,
                prodottiNonPagati.size(),
                calcolaTotale(prodottiNonPagati),
                LocalDateTime.now()
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
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine,idProdotto);

        OrdiniProdottiEntity prodottoOrdine = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato nell'ordine."));

        if(prodottoOrdine.getStatoPagato() == StatoPagato.NON_PAGATO){
            throw new RuntimeException("Il prodotto risulta ancora da pagare.");
        }

        prodottoOrdine.setStatoPagato(StatoPagato.NON_PAGATO);

        ordiniProdottiRepository.save(prodottoOrdine);
    }

    @Transactional
    public AnnullaPagamentoRisultatoDto annullaPagamentoTuttoOrdine(Long idOrdine) {

        // Verifica ordine
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new RuntimeException("Ordine inesistente.");
        }

        // Trova prodotti pagati
        List<OrdiniProdottiEntity> prodottiPagati = ordiniProdottiRepository
                .findByOrdineIdOrdineAndStatoPagato(idOrdine, StatoPagato.PAGATO);

        if (prodottiPagati.isEmpty()) {
            throw new RuntimeException("Nessun prodotto risulta pagato.");
        }

        // Annulla tutti i pagamenti e salva
        prodottiPagati.forEach(prodotto -> {
            prodotto.setStatoPagato(StatoPagato.NON_PAGATO);
        });

        ordiniProdottiRepository.saveAll(prodottiPagati);

        // Risultato
        return new AnnullaPagamentoRisultatoDto(
                idOrdine,
                prodottiPagati.size(),
                calcolaTotale(prodottiPagati),
                LocalDateTime.now()
        );
    }

}
