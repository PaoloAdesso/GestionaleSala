package it.paoloadesso.gestionetavoli.services;

import it.paoloadesso.gestionetavoli.dto.OrdineMinimalDTO;
import it.paoloadesso.gestionetavoli.dto.StatoOrdineETavoloResponseDTO;
import it.paoloadesso.gestionetavoli.dto.TavoloConOrdiniChiusiDTO;
import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.TavoliEntity;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import it.paoloadesso.gestionetavoli.enums.StatoTavolo;
import it.paoloadesso.gestionetavoli.mapper.OrdiniMapper;
import it.paoloadesso.gestionetavoli.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionetavoli.repositories.OrdiniRepository;
import it.paoloadesso.gestionetavoli.repositories.TavoliRepository;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdineService {

    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final TavoliRepository tavoliRepository;
    private final OrdiniMapper ordiniMapper;

    public OrdineService(OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository,
                         TavoliRepository tavoliRepository, OrdiniMapper ordiniMapper) {
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.tavoliRepository = tavoliRepository;
        this.ordiniMapper = ordiniMapper;
    }

    @Transactional
    public StatoOrdineETavoloResponseDTO chiudiOrdine(@Positive Long idOrdine) {

        // Verifica ordine
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);
        if (ordine == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "ORDINE_NON_TROVATO: Ordine con ID " + idOrdine + " non trovato"
            );
        }

        // Verifica stato ordine
        if (ordine.getStatoOrdine() == StatoOrdine.CHIUSO) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "ORDINE_GIA_CHIUSO: L'ordine con ID " + idOrdine + " risulta già chiuso"
            );
        }
        // Verifica che tutti i prodotti dell'ordine risultino pagati
        List<OrdiniProdottiEntity> prodottiNonPagati =
                ordiniProdottiRepository.findByOrdineIdOrdineAndStatoPagato(
                        idOrdine, StatoPagato.NON_PAGATO);

        if (!prodottiNonPagati.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "PRODOTTI_NON_PAGATI: I prodotti dell'ordine NON risultano tutti pagati"
            );
        }
        ordine.setStatoOrdine(StatoOrdine.CHIUSO);
        ordiniRepository.save(ordine);

        // trovo il tavolo
        TavoliEntity tavolo = ordine.getTavolo();
        // verifico che non ci siano altri ordini aperti
        List<OrdiniEntity> altriOrdiniAperti = ordiniRepository
                .findByTavoloIdAndStatoOrdineNot(tavolo.getId(), StatoOrdine.CHIUSO);

        boolean isAltriOrdiniAperti;

        if (altriOrdiniAperti.isEmpty()) {
            isAltriOrdiniAperti = false;
        } else {
            isAltriOrdiniAperti = true;
        }

        if (!isAltriOrdiniAperti) {
            tavolo.setStatoTavolo(StatoTavolo.LIBERO);
            tavoliRepository.save(tavolo);
        }

        return new StatoOrdineETavoloResponseDTO(
                idOrdine,
                ordine.getStatoOrdine(),
                isAltriOrdiniAperti,
                ordine.getTavolo().getId(),
                ordine.getTavolo().getStatoTavolo()
        );
    }


    public List<TavoloConOrdiniChiusiDTO> getOrdiniChiusi() {
        // Carica tutti gli ordini chiusi
        List<OrdiniEntity> ordiniChiusi = ordiniRepository.findByStatoOrdine(StatoOrdine.CHIUSO);

        // Raggruppa per tavolo
        Map<Long, List<OrdiniEntity>> ordiniPerTavolo = ordiniChiusi.stream()
                .collect(Collectors.groupingBy(ordine -> ordine.getTavolo().getId()));

        // Converti in DTO
        return ordiniPerTavolo.values().stream()
                .map(ordiniEntities -> {
                    TavoliEntity tavolo = ordiniEntities.getFirst().getTavolo();

                    List<OrdineMinimalDTO> ordini = ordiniEntities.stream()
                            .map(ordine -> new OrdineMinimalDTO(
                                    ordine.getIdOrdine(),
                                    ordine.getDataOrdine(),
                                    ordine.getStatoOrdine()
                            ))
                            .collect(Collectors.toList());

                    return new TavoloConOrdiniChiusiDTO(
                            tavolo.getId(),
                            tavolo.getNumeroNomeTavolo(),
                            tavolo.getStatoTavolo(),
                            ordini
                    );
                })
                .collect(Collectors.toList());
    }

    public List<TavoloConOrdiniChiusiDTO> getOrdiniChiusiDiOggi() {
        // Carica tutti gli ordini chiusi
        List<OrdiniEntity> ordiniChiusiDiOggi = ordiniRepository.findByStatoOrdineAndDataOrdine(StatoOrdine.CHIUSO, LocalDate.now());

        // Raggruppa per tavolo
        Map<Long, List<OrdiniEntity>> ordiniPerTavolo = ordiniChiusiDiOggi.stream()
                .collect(Collectors.groupingBy(ordine -> ordine.getTavolo().getId()));

        // Converti in DTO
        return ordiniPerTavolo.values().stream()
                .map(ordiniEntities -> {
                    TavoliEntity tavolo = ordiniEntities.getFirst().getTavolo();

                    List<OrdineMinimalDTO> ordini = ordiniEntities.stream()
                            .map(ordine -> new OrdineMinimalDTO(
                                    ordine.getIdOrdine(),
                                    ordine.getDataOrdine(),
                                    ordine.getStatoOrdine()
                            ))
                            .collect(Collectors.toList());

                    return new TavoloConOrdiniChiusiDTO(
                            tavolo.getId(),
                            tavolo.getNumeroNomeTavolo(),
                            tavolo.getStatoTavolo(),
                            ordini
                    );
                })
                .collect(Collectors.toList());
    }

}
