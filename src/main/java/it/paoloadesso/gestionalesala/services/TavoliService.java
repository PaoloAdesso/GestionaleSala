package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import it.paoloadesso.gestionalesala.exceptionhandling.ModificaVuotaException;
import it.paoloadesso.gestionalesala.exceptionhandling.TavoloEliminatoException;
import it.paoloadesso.gestionalesala.exceptionhandling.TavoloNotFoundException;
import it.paoloadesso.gestionalesala.mapper.TavoliMapper;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import it.paoloadesso.gestionalesala.repositories.TavoliRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class TavoliService {

    private final TavoliRepository tavoliRepository;
    private final TavoliMapper tavoliMapper;

    private static final Logger log = LoggerFactory.getLogger(TavoliService.class);
    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;

    public TavoliService(TavoliRepository tavoliRepository, TavoliMapper tavoliMapper, OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository) {
        this.tavoliRepository = tavoliRepository;
        this.tavoliMapper = tavoliMapper;
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
    }

    @Transactional
    public TavoliDTO creaTavolo(CreaTavoliRequestDTO dto) {
        if (tavoliRepository.existsByNumeroNomeTavoloIgnoreCase(dto.getNumeroNomeTavolo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Esiste già un tavolo con numero/nome «" + dto.getNumeroNomeTavolo() + "»"
            );
        }

        TavoliEntity tavolo = tavoliRepository.save(tavoliMapper.createTavoliDtoToEntity(dto));
        return tavoliMapper.simpleEntityToDto(tavolo);
    }

    public List<TavoliDTO> getTavoli() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findAll();
        List<TavoliDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.simpleEntityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    public List<TavoliDTO> getTavoliLiberi() {
        List<TavoliEntity> listaTavoli = tavoliRepository.findByStatoTavolo(StatoTavolo.LIBERO);
        List<TavoliDTO> tavoliResponseDto;
        tavoliResponseDto = listaTavoli.stream()
                .map(el -> tavoliMapper.simpleEntityToDto(el))
                .toList();
        return tavoliResponseDto;
    }

    @Transactional
    public void eliminaFisicamenteTavoloByIdERelativiOrdiniCollegati(Long idTavolo) {
        // Cerco il tavolo e se non c'è restituisco relativo messaggio
        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> new EntityNotFoundException("Tavolo con ID " + idTavolo + " non trovato"));
        // Elimino il tavolo trovato
        tavoliRepository.deletePhysically(tavolo.getId());
    }

    @Transactional
    public void eliminaTavoloByIdERelativiOrdiniCollegati(Long idTavolo) {
        boolean esisteGiaCancellato = tavoliRepository.existsDeletedTavolo(idTavolo);

        if (esisteGiaCancellato) {
            throw new IllegalStateException("Tavolo con ID " + idTavolo + " già cancellato");
        }

        TavoliEntity tavolo = tavoliRepository.findById(idTavolo)
                .orElseThrow(() -> new EntityNotFoundException("Tavolo con ID " + idTavolo + " non trovato"));

        List<OrdiniEntity> ordiniCollegati = ordiniRepository.findByTavoloId(idTavolo);

        List<OrdiniProdottiEntity> prodottiOrdinatiCollegati = ordiniProdottiRepository.findByOrdineTavoloId(idTavolo);

        if (!prodottiOrdinatiCollegati.isEmpty()) {
            ordiniProdottiRepository.deleteAll(prodottiOrdinatiCollegati);
            log.info("Eliminati (soft delete) {} prodotti ordinati collegati agli ordini del tavolo con ID {}"
                    , prodottiOrdinatiCollegati.size(), idTavolo);
        }

        if (!ordiniCollegati.isEmpty()) {
            ordiniRepository.deleteAll(ordiniCollegati);
            log.info("Eliminati (soft delete) {} ordini collegati al tavolo con ID {}", ordiniCollegati.size(), idTavolo);
        }

        tavoliRepository.delete(tavolo);
        log.info("Tavolo con ID {} cancellato (soft delete)", idTavolo);
    }

    @Scheduled(cron = "${ristorante.cron-reset-tavoli}", zone = "Europe/Rome")
    public void liberaTuttiITavoliProgrammato() {
        log.info("Reset automatico tavoli - Inizio turno lavorativo");
        resetStatoTavolo();
    }

    public void liberaTuttiITavoli() {
        log.info("Reset manuale tavoli richiesto");
        resetStatoTavolo();
    }

    private void resetStatoTavolo() {
        List<TavoliEntity> tuttiITavoli = tavoliRepository.findAll();

        tuttiITavoli.forEach(tavolo -> tavolo.setStatoTavolo(StatoTavolo.LIBERO));
        // Alternativa con Stream (commentata)
//        List<TavoliEntity> tavoliAggiornati = tuttiITavoli.stream()
//                .map(tavolo -> {
//                    tavolo.setStatoTavolo(StatoTavolo.LIBERO);
//                    return tavolo;
//                })
//                .toList();
//
//        tavoliRepository.saveAll(tavoliAggiornati);
        tavoliRepository.saveAll(tuttiITavoli);
        log.info("Reset completato. Lo stato di tutti i tavoli è stato reimpostato a «LIBERO».");
    }

    public TavoliConDettaglioDeleteDTO ripristinaSingoloTavolo(@Positive Long idTavolo) {
        TavoliEntity tavoloDaRipristinare = tavoliRepository.findByIdInclusoEliminati(idTavolo)
                .orElseThrow(() -> new EntityNotFoundException("Tavolo con ID " + idTavolo + " non trovato."));

        if (tavoloDaRipristinare.getDeleted() == false) {
            throw new IllegalStateException("Il tavolo con ID " + idTavolo + " non risulta eliminato," +
                    " pertanto non è necessario ripristinarlo.");
        }

        tavoloDaRipristinare.setDeleted(false);
        tavoloDaRipristinare.setDeletedAt(null);
        TavoliEntity tavoloRipristinato = tavoliRepository.save(tavoloDaRipristinare);

        return tavoliMapper.tavoliEntityToTavoliConDettaglioDeleteDto(tavoloRipristinato);
    }

    public List<TavoliDTO> getAllTavoliEliminati() {
        List<TavoliEntity> entities = tavoliRepository.findAllTavoliEliminati();
        return entities.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }

    @Transactional
    public RisultatoModificaTavoloDTO modificaTavolo(Long tavoloId, ModificaTavoloRequestDTO modificaDto) {

        if (modificaDto.isEmpty()) {
            throw new ModificaVuotaException();
        }

        // Trova il tavolo originale
        TavoliEntity tavoloOriginale = tavoliRepository.findById(tavoloId)
                .orElseThrow(() -> new TavoloNotFoundException(tavoloId));

        if (tavoloOriginale.getDeleted()) {
            throw new TavoloEliminatoException(tavoloId);
        }

        // Clona per confronto
        TavoliEntity tavoloClone = new TavoliEntity();
        tavoloClone.setNumeroNomeTavolo(tavoloOriginale.getNumeroNomeTavolo());
        tavoloClone.setStatoTavolo(tavoloOriginale.getStatoTavolo());

        // Applica le modifiche
        if (modificaDto.getNumeroNomeTavolo() != null) {
            tavoloOriginale.setNumeroNomeTavolo(modificaDto.getNumeroNomeTavolo());
        }

        if (modificaDto.getStatoTavolo() != null) {
            tavoloOriginale.setStatoTavolo(modificaDto.getStatoTavolo());
        }

        // Salva le modifiche
        TavoliEntity tavoloModificato = tavoliRepository.save(tavoloOriginale);

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

        return new RisultatoModificaTavoloDTO(
                tavoliMapper.simpleEntityToDto(tavoloModificato),
                campiModificati,
                operazioneCompleta,
                messaggio
        );
    }

    public List<TavoliDTO> getAllTavoliAttivi() {
        List<TavoliEntity> entities = tavoliRepository.findAll();

        return entities.stream()
                .map(tavoliMapper::simpleEntityToDto)
                .toList();
    }
}
