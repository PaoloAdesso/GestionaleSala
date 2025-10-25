package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import it.paoloadesso.gestionalesala.exceptionhandling.*;
import it.paoloadesso.gestionalesala.mapper.TavoliMapper;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import it.paoloadesso.gestionalesala.repositories.TavoliRepository;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TavoliService {

    private static final Logger log = LoggerFactory.getLogger(TavoliService.class);
    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;
    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper,
                         OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
    }

    @Transactional
    public TavoliDTO creaTavolo(CreaTavoliRequestDTO dto) {
        log.info("Tentativo di creazione tavolo: {}", dto.getNumeroNomeTavolo());

        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            log.warn("Tentativo di creare un tavolo già esistente: {}", dto.getNumeroNomeTavolo());
            throw new EntitaGiaEsistenteException("tavolo", "numero/nome «" + dto.getNumeroNomeTavolo() + "»");
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        log.info("Tavolo creato con successo - ID: {}, Nome: {}", tavolo.getId(), tavolo.getNumeroNomeTavolo());

        return tavoliMapper.simpleEntityToDto(tavolo);
    }

    public List<TavoliDTO> getTavoli() {
        log.debug("Richiesta lista di tutti i tavoli");
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        log.info("Trovati {} tavoli", listaTavoli.size());

        return listaTavoli.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }

    public List<TavoliDTO> getTavoliLiberi() {
        log.debug("Richiesta lista tavoli liberi");
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        log.info("Trovati {} tavoli liberi", listaTavoli.size());

        return listaTavoli.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }

    @Transactional
    public void eliminaFisicamenteTavoloByIdERelativiOrdiniCollegati(Long idTavolo) {
        log.info("Tentativo di eliminazione fisica tavolo con ID: {}", idTavolo);

        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per eliminazione fisica", idTavolo);
                    return new EntitaNonTrovataException("Tavolo", idTavolo);
                });

        tavoliRepository.deletePhysically(tavolo.getId());
        log.info("Tavolo con ID {} eliminato fisicamente", idTavolo);
    }

    @Transactional
    public void eliminaTavoloByIdERelativiOrdiniCollegati(Long idTavolo) {
        log.info("Tentativo di eliminazione tavolo con ID: {}", idTavolo);

        boolean esisteGiaCancellato = tavoliRepository.existsDeletedTavolo(idTavolo);

        if (esisteGiaCancellato) {
            log.warn("Tentativo di eliminare un tavolo già cancellato - ID: {}", idTavolo);
            throw new StatoNonValidoException("eliminare il tavolo", "già cancellato");
        }

        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per eliminazione", idTavolo);
                    return new EntitaNonTrovataException("Tavolo", idTavolo);
                });

        List<OrdiniEntity> ordiniCollegati = ordiniRepository.findByTavoloId(idTavolo);
        List<OrdiniProdottiEntity> prodottiOrdinatiCollegati = ordiniProdottiRepository.findByOrdineTavoloId(idTavolo);

        if (!prodottiOrdinatiCollegati.isEmpty()) {
            ordiniProdottiRepository.deleteAll(prodottiOrdinatiCollegati);
            log.info("Eliminati (soft delete) {} prodotti ordinati collegati agli ordini del tavolo con ID {}",
                    prodottiOrdinatiCollegati.size(), idTavolo);
        }

        if (!ordiniCollegati.isEmpty()) {
            ordiniRepository.deleteAll(ordiniCollegati);
            log.info("Eliminati (soft delete) {} ordini collegati al tavolo con ID {}", ordiniCollegati.size(), idTavolo);
        }

        tavoliRepository.delete(tavolo);
        log.info("Tavolo con ID {} cancellato (soft delete)", idTavolo);
    }

    @Transactional
    public void liberaTuttiITavoli() {
        log.info("Reset tavoli richiesto");

        List<TavoliEntity> tuttiITavoli = tavoliRepository.findAll();

        tuttiITavoli.forEach(tavolo -> tavolo.setStatoTavolo(StatoTavolo.LIBERO));
        tavoliRepository.saveAll(tuttiITavoli);

        log.info("Reset completato. Lo stato di tutti i {} tavoli è stato reimpostato a «LIBERO».", tuttiITavoli.size());
    }

    public TavoliConDettaglioDeleteDTO ripristinaSingoloTavolo(@Positive Long idTavolo) {
        log.info("Tentativo di ripristino tavolo con ID: {}", idTavolo);

        TavoliEntity tavoloDaRipristinare = tavoliRepository.findByIdInclusoEliminati(idTavolo)
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per ripristino", idTavolo);
                    return new EntitaNonTrovataException("Tavolo", idTavolo);
                });

        if (!tavoloDaRipristinare.getDeleted()) {
            log.warn("Tentativo di ripristinare un tavolo non eliminato - ID: {}", idTavolo);
            throw new StatoNonValidoException("ripristinare il tavolo", "non eliminato");
        }

        tavoloDaRipristinare.setDeleted(false);
        tavoloDaRipristinare.setDeletedAt(null);
        TavoliEntity tavoloRipristinato = tavoliRepository.save(tavoloDaRipristinare);

        log.info("Tavolo ripristinato con successo - ID: {}", idTavolo);
        return tavoliMapper.tavoliEntityToTavoliConDettaglioDeleteDto(tavoloRipristinato);
    }

    public List<TavoliDTO> getAllTavoliEliminati() {
        log.debug("Richiesta lista di tutti i tavoli eliminati");
        List<TavoliEntity> entities = tavoliRepository.findAllTavoliEliminati();
        log.info("Trovati {} tavoli eliminati", entities.size());

        return entities.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }

    @Transactional
    public RisultatoModificaTavoloDTO modificaTavolo(Long tavoloId, ModificaTavoloRequestDTO modificaDto) {
        log.info("Tentativo di modifica tavolo con ID: {}", tavoloId);

        if (modificaDto.isEmpty()) {
            log.warn("Tentativo di modifica tavolo con dati vuoti - ID: {}", tavoloId);
            throw new ModificaVuotaException();
        }

        // Trova il tavolo originale
        TavoliEntity tavoloOriginale = tavoliRepository.findById(tavoloId)
                .orElseThrow(() -> {
                    log.error("Tavolo con ID {} non trovato per modifica", tavoloId);
                    return new EntitaNonTrovataException("Tavolo", tavoloId);
                });

        if (tavoloOriginale.getDeleted()) {
            log.warn("Tentativo di modificare un tavolo eliminato - ID: {}", tavoloId);
            throw new StatoNonValidoException("modificare il tavolo", "eliminato");
        }

        // Clona per confronto
        TavoliEntity tavoloClone = new TavoliEntity();
        tavoloClone.setNumeroNomeTavolo(tavoloOriginale.getNumeroNomeTavolo());
        tavoloClone.setStatoTavolo(tavoloOriginale.getStatoTavolo());

        // Applica le modifiche
        if (modificaDto.getNumeroNomeTavolo() != null) {
            log.debug("Modifica numero/nome tavolo ID {}: '{}' -> '{}'",
                    tavoloId, tavoloOriginale.getNumeroNomeTavolo(), modificaDto.getNumeroNomeTavolo());
            tavoloOriginale.setNumeroNomeTavolo(modificaDto.getNumeroNomeTavolo());
        }

        if (modificaDto.getStatoTavolo() != null) {
            log.debug("Modifica stato tavolo ID {}: {} -> {}",
                    tavoloId, tavoloOriginale.getStatoTavolo(), modificaDto.getStatoTavolo());
            tavoloOriginale.setStatoTavolo(modificaDto.getStatoTavolo());
        }

        // Salva le modifiche
        TavoliEntity tavoloModificato = tavoliRepository.save(tavoloOriginale);
        log.info("Tavolo modificato con successo - ID: {}", tavoloId);

        // Crea il risultato dettagliato
        return creaRisultatoModifica(tavoloClone, tavoloModificato, modificaDto);
    }

    private RisultatoModificaTavoloDTO creaRisultatoModifica(
            TavoliEntity tavoloOriginale,
            TavoliEntity tavoloModificato,
            ModificaTavoloRequestDTO modificaDto) {

        List<String> campiModificati = new ArrayList<>();

        // Controllo modifica NUMERO/NOME
        if (modificaDto.getNumeroNomeTavolo() != null &&
                !modificaDto.getNumeroNomeTavolo().equals(tavoloOriginale.getNumeroNomeTavolo())) {
            campiModificati.add("numeroNomeTavolo");
        }

        // Controllo modifica STATO
        if (modificaDto.getStatoTavolo() != null &&
                !modificaDto.getStatoTavolo().equals(tavoloOriginale.getStatoTavolo())) {
            campiModificati.add("statoTavolo");
        }

        // Costruzione messaggio
        String messaggio;
        if (campiModificati.isEmpty()) {
            messaggio = "Nessuna modifica applicata - i valori forniti sono identici a quelli esistenti";
        } else if (campiModificati.size() == 1) {
            messaggio = "Campo modificato: " + campiModificati.get(0);
        } else {
            messaggio = "Campi modificati: " + String.join(", ", campiModificati);
        }

        boolean operazioneCompleta = true;

        log.info("Risultato modifica tavolo ID {}: {}", tavoloModificato.getId(), messaggio);

        return new RisultatoModificaTavoloDTO(
                tavoliMapper.simpleEntityToDto(tavoloModificato),
                campiModificati,
                operazioneCompleta,
                messaggio
        );
    }

    public List<TavoliDTO> getAllTavoliAttivi() {
        log.debug("Richiesta lista di tutti i tavoli attivi");
        List<TavoliEntity> entities = tavoliRepository.findAll();
        log.info("Trovati {} tavoli attivi", entities.size());

        return entities.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }
}
