package it.paoloadesso.gestionetavoli.services;

import it.paoloadesso.gestionetavoli.dto.OrdineConProdottiDTO;
import it.paoloadesso.gestionetavoli.dto.ProdottoNonPagatoDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoConDettagliOrdineDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloApertoDTO;
import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.TavoliEntity;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import it.paoloadesso.gestionetavoli.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionetavoli.repositories.OrdiniRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CassaService {

    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    public CassaService(OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository) {
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
    }

    public List<TavoloApertoDTO> getTavoliAperti() {
        // Uso il metodo di aiuto per ottenere la mappa raggruppata per tavolo
        Map<TavoliEntity, List<OrdiniEntity>> ordiniPerTavolo = getOrdiniApertiRaggruppatiPerTavolo();

        // Per ogni tavolo nella mappa, creo un DTO semplice (senza dettagli ordini)
        return ordiniPerTavolo.keySet().stream()
                .map(tavolo -> new TavoloApertoDTO(
                        tavolo.getId(),
                        tavolo.getNumeroNomeTavolo(),
                        tavolo.getStatoTavolo()
                ))
                .collect(Collectors.toList());
    }

    public List<TavoloApertoConDettagliOrdineDTO> getDettaglioTavoliAperti() {
        // Uso lo stesso metodo di aiuto per ottenere la mappa raggruppata
        Map<TavoliEntity, List<OrdiniEntity>> ordiniPerTavolo = getOrdiniApertiRaggruppatiPerTavolo();

        // Per ogni entry della mappa (tavolo + lista ordini)
        return ordiniPerTavolo.entrySet().stream()
                .map(entry -> {
                    TavoliEntity tavolo = entry.getKey();
                    List<OrdiniEntity> ordiniDelTavolo = entry.getValue();

                    // Trasformo ogni ordine del tavolo in DTO completo usando il metodo di aiuto
                    List<OrdineConProdottiDTO> ordiniDTO = ordiniDelTavolo.stream()
                            .map(this::creaOrdineConProdottiDto)
                            .collect(Collectors.toList());

                    // Calcolo il totale complessivo di tutti gli ordini del tavolo
                    BigDecimal totaleComplessivo = calcolaTotaleComplessivo(ordiniDTO);

                    return new TavoloApertoConDettagliOrdineDTO(
                            tavolo.getId(),
                            tavolo.getNumeroNomeTavolo(),
                            ordiniDTO,
                            totaleComplessivo
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * Metodo di aiuto: trasforma una singola riga ordini_prodotti in DTO prodotto.
     * Calcola il subtotale moltiplicando prezzo per quantità.
     */
    private ProdottoNonPagatoDTO creaProdottoNonPagato(OrdiniProdottiEntity prodotto) {
        return new ProdottoNonPagatoDTO(
                prodotto.getProdotto().getId(),
                prodotto.getProdotto().getNome(),
                prodotto.getProdotto().getPrezzo(),
                prodotto.getQuantitaProdotto(),
                // Calcolo subtotale: prezzo * quantità
                prodotto.getProdotto().getPrezzo()
                        .multiply(BigDecimal.valueOf(prodotto.getQuantitaProdotto()))
        );
    }

    /**
     * Metodo di aiuto: trasforma un ordine completo in DTO con tutti i suoi prodotti non pagati.
     * Prima recupera i prodotti non pagati, poi li trasforma in DTO e calcola il totale.
     */
    private OrdineConProdottiDTO creaOrdineConProdottiDto(OrdiniEntity ordine) {
        // Cerco tutti i prodotti non pagati per questo ordine
        List<OrdiniProdottiEntity> prodottiNonPagati =
                ordiniProdottiRepository.findByOrdineAndStatoPagato(ordine, StatoPagato.NON_PAGATO);

        // Trasformo ogni prodotto in DTO usando il metodo di aiuto
        List<ProdottoNonPagatoDTO> prodottiDto = prodottiNonPagati.stream()
                .map(this::creaProdottoNonPagato)
                .collect(Collectors.toList());

        // Sommo tutti i subtotali per avere il totale dell'ordine
        BigDecimal totaleOrdine = prodottiDto.stream()
                .map(ProdottoNonPagatoDTO::getSubtotale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new OrdineConProdottiDTO(
                ordine.getIdOrdine(),
                totaleOrdine,
                prodottiDto
        );
    }

    /**
     * Metodo di aiuto: somma tutti i totali di una lista di ordini per avere il totale complessivo.
     * Uso reduce per sommare tutti i BigDecimal in un colpo solo.
     */
    private BigDecimal calcolaTotaleComplessivo(List<OrdineConProdottiDTO> ordini) {
        return ordini.stream()
                .map(OrdineConProdottiDTO::getTotaleOrdine)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Metodo di aiuto: trova tutti gli ordini aperti e li raggruppa per tavolo.
     * Prima filtro gli ordini che hanno prodotti non pagati, poi li raggruppo per tavolo.
     */
    private Map<TavoliEntity, List<OrdiniEntity>> getOrdiniApertiRaggruppatiPerTavolo() {
        // Cerco tutti gli ordini che NON sono chiusi
        List<OrdiniEntity> ordiniEntity = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);

        return ordiniEntity.stream()
                // Tengo solo gli ordini che hanno almeno un prodotto non pagato
                .filter(ordine -> !ordiniProdottiRepository
                        .findByOrdineAndStatoPagato(ordine, StatoPagato.NON_PAGATO)
                        .isEmpty())
                // Raggruppo per tavolo: il risultato è una mappa tavolo → lista ordini
                .collect(Collectors.groupingBy(OrdiniEntity::getTavolo));
    }
}
