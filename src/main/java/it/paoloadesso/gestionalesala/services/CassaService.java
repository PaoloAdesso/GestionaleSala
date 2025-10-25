package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.OrdineConProdottiDTO;
import it.paoloadesso.gestionalesala.dto.ProdottoNonPagatoDTO;
import it.paoloadesso.gestionalesala.dto.TavoloApertoConDettagliOrdineDTO;
import it.paoloadesso.gestionalesala.dto.TavoloApertoDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import it.paoloadesso.gestionalesala.enums.StatoPagato;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CassaService {

    private static final Logger log = LoggerFactory.getLogger(CassaService.class);

    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    public CassaService(OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository) {
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
    }

    /**
     * Questo metodo restituisce tutti i tavoli che hanno ordini aperti con prodotti non pagati.
     * È la vista principale della cassa: mostra quali tavoli devono ancora pagare.
     *
     * Logica: prendo tutti i prodotti non pagati, raggruppo per tavolo, e restituisco
     * solo i tavoli che hanno almeno un prodotto non pagato in un ordine aperto.
     */
    public List<TavoloApertoDTO> getTavoliAperti() {
        log.debug("Richiesta tavoli aperti per cassa");

        // Prendo tutti gli ordini non chiusi
        List<OrdiniEntity> tavoliAperti = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);

        // Filtro solo gli ordini con prodotti non pagati, estraggo i tavoli e tolgo duplicati; infine li trasformo in DTO
        List<TavoloApertoDTO> tavoliApertiDto = tavoliAperti.stream()
                .filter(ordine -> !ordiniProdottiRepository.findByOrdineAndStatoPagato(ordine, StatoPagato.NON_PAGATO)
                        .isEmpty())
                .map(OrdiniEntity::getTavolo)
                .distinct()
                .map(tavolo -> new TavoloApertoDTO(
                        tavolo.getId(),
                        tavolo.getNumeroNomeTavolo(),
                        tavolo.getStatoTavolo()
                ))
                .toList();

        log.info("Trovati {} tavoli aperti con prodotti da pagare", tavoliApertiDto.size());
        return tavoliApertiDto;
    }

    public List<TavoloApertoConDettagliOrdineDTO> getTavoliApertiConDettagliOrdini() {
        log.debug("Richiesta tavoli aperti con dettagli ordini per cassa");

        // Creo una mappa tavolo → ordini filtrando quelli con prodotti non pagati
        Map<TavoliEntity, List<OrdiniEntity>> ordiniPerTavolo = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO).stream()
                .filter(ordine -> !ordiniProdottiRepository
                        .findByOrdineAndStatoPagato(ordine, StatoPagato.NON_PAGATO)
                        .isEmpty())
                .collect(Collectors.groupingBy(OrdiniEntity::getTavolo));

        // Per ogni tavolo costruisco il DTO dettagliato con ordini e calcolo il totale complessivo
        List<TavoloApertoConDettagliOrdineDTO> listaDto = ordiniPerTavolo.entrySet().stream()
                .map(entry -> {
                    TavoliEntity tavolo = entry.getKey();
                    List<OrdiniEntity> ordiniDelTavolo = entry.getValue();

                    // Trasformo tutti gli ordini del tavolo in DTO completi con prodotti
                    List<OrdineConProdottiDTO> ordiniConProdottiDto = ordiniDelTavolo.stream()
                            .map(this::creaOrdineConProdotti)
                            .toList();

                    // Calcolo il totale del tavolo sommando i totali degli ordini
                    BigDecimal totaleTavolo = ordiniConProdottiDto.stream()
                            .map(OrdineConProdottiDTO::getTotaleOrdine)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    log.debug("Tavolo {} - Totale: €{}", tavolo.getNumeroNomeTavolo(), totaleTavolo);

                    return new TavoloApertoConDettagliOrdineDTO(
                            tavolo.getId(),
                            tavolo.getNumeroNomeTavolo(),
                            ordiniConProdottiDto,
                            totaleTavolo
                    );
                })
                .toList();

        log.info("Generati dettagli per {} tavoli aperti", listaDto.size());
        return listaDto;
    }

    private ProdottoNonPagatoDTO creaProdottoNonPagato(OrdiniProdottiEntity prodotto) {
        // Creo un DTO prodotto con dettagli e calcolo il subtotale moltiplicando prezzo e quantità
        return new ProdottoNonPagatoDTO(
                prodotto.getProdotto().getId(),
                prodotto.getProdotto().getNome(),
                prodotto.getProdotto().getPrezzo(),
                prodotto.getQuantitaProdotto(),
                prodotto.getProdotto().getPrezzo().multiply(BigDecimal.valueOf(prodotto.getQuantitaProdotto())));
    }

    private OrdineConProdottiDTO creaOrdineConProdotti(OrdiniEntity ordine) {
        // Prendo tutti i prodotti non pagati dell'ordine
        List<OrdiniProdottiEntity> prodottiNonPagati = ordiniProdottiRepository.findByOrdineAndStatoPagato(ordine, StatoPagato.NON_PAGATO);

        // Li trasformo in DTO
        List<ProdottoNonPagatoDTO> prodottiDto = prodottiNonPagati.stream()
                .map(this::creaProdottoNonPagato)
                .toList();

        // Calcolo il totale dell'ordine sommando tutti i subtotali dei prodotti
        BigDecimal totaleOrdine = prodottiDto.stream()
                .map(ProdottoNonPagatoDTO::getSubtotale)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Creo e ritorno il DTO ordine con totale e lista prodotti
        return new OrdineConProdottiDTO(
                ordine.getIdOrdine(),
                totaleOrdine,
                prodottiDto
        );
    }
}
