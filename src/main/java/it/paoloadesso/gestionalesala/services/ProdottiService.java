package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.exceptionhandling.*;
import it.paoloadesso.gestionalesala.mapper.ProdottiMapper;
import it.paoloadesso.gestionalesala.repositories.ProdottiRepository;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProdottiService {

    private static final Logger log = LoggerFactory.getLogger(ProdottiService.class);
    private final ProdottiRepository prodottiRepository;
    private final ProdottiMapper prodottiMapper;

    public ProdottiService(ProdottiRepository prodottiRepository, ProdottiMapper prodottiMapper) {
        this.prodottiRepository = prodottiRepository;
        this.prodottiMapper = prodottiMapper;
    }

    @Transactional
    public ProdottiDTO creaProdotto(CreaProdottiDTO dto) {
        log.info("Tentativo di creazione prodotto: {}", dto.getNome());

        if (prodottiRepository.existsByNomeIgnoreCase(dto.getNome())) {
            log.warn("Tentativo di creare un prodotto già esistente: {}", dto.getNome());
            throw new EntitaGiaEsistenteException("prodotto", "nome «" + dto.getNome() + "»");
        }

        ProdottiEntity prodotto = prodottiRepository.save(prodottiMapper.createProdottiDtoToEntity(dto));
        log.info("Prodotto creato con successo - ID: {}, Nome: {}", prodotto.getId(), prodotto.getNome());

        return prodottiMapper.prodottiEntityToProdottiDto(prodotto);
    }

    public List<ProdottiDTO> getAllProdotti() {
        log.debug("Richiesta lista di tutti i prodotti");
        List<ProdottiEntity> entities = prodottiRepository.findAll();
        log.info("Trovati {} prodotti", entities.size());

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<String> getAllCategorie() {
        log.debug("Richiesta lista di tutte le categorie");
        List<String> categorie = prodottiRepository.findAllCategorieDistinct();
        log.info("Trovate {} categorie distinte", categorie.size());
        return categorie;
    }

    /**
     * Questo metodo cerca prodotti il cui nome contiene la stringa passata.
     * Ad esempio: se cerco "pizza" trovo "Pizza Margherita", "Pizza 4 Formaggi", ecc.
     * La ricerca è case-insensitive (non fa differenza tra maiuscole e minuscole).
     */
    public List<ProdottiDTO> getProdottiByContainingNome(@NotBlank String nomeProdotto) {
        log.debug("Ricerca prodotti per nome contenente: {}", nomeProdotto);

        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            log.warn("Tentativo di ricerca con nome prodotto vuoto");
            throw new CampoVuotoException("nome del prodotto");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCase(nomeProdotto.trim());
        log.info("Trovati {} prodotti che contengono '{}'", entities.size(), nomeProdotto);

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<ProdottiConDettaglioDeleteDTO> getTuttiProdottiAttiviEdEliminatiByContainingNome(@NotBlank String nomeProdotto) {
        log.debug("Ricerca prodotti attivi ed eliminati per nome contenente: {}", nomeProdotto);

        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            log.warn("Tentativo di ricerca con nome prodotto vuoto");
            throw new CampoVuotoException("nome del prodotto");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCaseNative(nomeProdotto.trim());
        log.info("Trovati {} prodotti (attivi ed eliminati) che contengono '{}'", entities.size(), nomeProdotto);

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiConDettaglioDeleteDto)
                .toList();
    }

    public List<ProdottiDTO> getAllProdottiEliminati() {
        log.debug("Richiesta lista di tutti i prodotti eliminati");
        List<ProdottiEntity> entities = prodottiRepository.findAllProdottiEliminati();
        log.info("Trovati {} prodotti eliminati", entities.size());

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<ProdottiDTO> getProdottiByContainingCategoria(String nomeCategoria) {
        log.debug("Ricerca prodotti per categoria contenente: {}", nomeCategoria);

        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            log.warn("Tentativo di ricerca con nome categoria vuoto");
            throw new CampoVuotoException("nome della categoria");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(nomeCategoria.trim());
        log.info("Trovati {} prodotti nella categoria '{}'", entities.size(), nomeCategoria);

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    @Transactional
    public void deleteProdotto(@Positive Long idProdotto) {
        log.info("Tentativo di eliminazione prodotto con ID: {}", idProdotto);

        boolean esisteGiaCancellato = prodottiRepository.existsDeletedProdotto(idProdotto);

        if (esisteGiaCancellato) {
            log.warn("Tentativo di eliminare un prodotto già cancellato - ID: {}", idProdotto);
            throw new StatoNonValidoException("eliminare il prodotto", "già cancellato");
        }

        ProdottiEntity prodotto = prodottiRepository.findById(idProdotto)
                .orElseThrow(() -> {
                    log.error("Prodotto con ID {} non trovato per eliminazione", idProdotto);
                    return new EntitaNonTrovataException("Prodotto", idProdotto);
                });

        prodottiRepository.delete(prodotto);
        log.info("Prodotto con ID {} eliminato con successo (soft delete)", idProdotto);
    }

    public ProdottiConDettaglioDeleteDTO ripristinaSingoloProdotto(Long idProdotto) {
        log.info("Tentativo di ripristino prodotto con ID: {}", idProdotto);

        ProdottiEntity prodottoDaRipristinare = prodottiRepository.findByIdInclusoEliminati(idProdotto)
                .orElseThrow(() -> {
                    log.error("Prodotto con ID {} non trovato per ripristino", idProdotto);
                    return new EntitaNonTrovataException("Prodotto", idProdotto);
                });

        if (!prodottoDaRipristinare.getDeleted()) {
            log.warn("Tentativo di ripristinare un prodotto non eliminato - ID: {}, Nome: {}",
                    idProdotto, prodottoDaRipristinare.getNome());
            throw new StatoNonValidoException("ripristinare il prodotto", "non eliminato");
        }

        prodottoDaRipristinare.setDeleted(false);
        prodottoDaRipristinare.setDeletedAt(null);
        ProdottiEntity prodottoRipristinato = prodottiRepository.save(prodottoDaRipristinare);

        log.info("Prodotto ripristinato con successo - ID: {}, Nome: {}",
                idProdotto, prodottoRipristinato.getNome());

        return prodottiMapper.prodottiEntityToProdottiConDettaglioDeleteDto(prodottoRipristinato);
    }

    @Transactional
    public RisultatoModificaProdottoDTO modificaProdotto(Long prodottoId, ModificaProdottoRequestDTO modificaDto) {
        log.info("Tentativo di modifica prodotto con ID: {}", prodottoId);

        if (modificaDto.isEmpty()) {
            log.warn("Tentativo di modifica prodotto con dati vuoti - ID: {}", prodottoId);
            throw new ModificaVuotaException();
        }

        // Salva lo stato originale PRIMA delle modifiche
        ProdottiEntity prodottoOriginale = prodottiRepository.findById(prodottoId)
                .orElseThrow(() -> {
                    log.error("Prodotto con ID {} non trovato per modifica", prodottoId);
                    return new EntitaNonTrovataException("Prodotto", prodottoId);
                });

        if (prodottoOriginale.getDeleted()) {
            log.warn("Tentativo di modificare un prodotto eliminato - ID: {}", prodottoId);
            throw new StatoNonValidoException("modificare il prodotto", "eliminato");
        }

        // Clona l'entità originale per confronto
        ProdottiEntity prodottoClone = new ProdottiEntity();
        prodottoClone.setNome(prodottoOriginale.getNome());
        prodottoClone.setCategoria(prodottoOriginale.getCategoria());
        prodottoClone.setPrezzo(prodottoOriginale.getPrezzo());

        // Applica le modifiche
        if (modificaDto.getNome() != null) {
            log.debug("Modifica nome prodotto ID {}: '{}' -> '{}'",
                    prodottoId, prodottoOriginale.getNome(), modificaDto.getNome());
            prodottoOriginale.setNome(modificaDto.getNome());
        }

        if (modificaDto.getCategoria() != null) {
            log.debug("Modifica categoria prodotto ID {}: '{}' -> '{}'",
                    prodottoId, prodottoOriginale.getCategoria(), modificaDto.getCategoria());
            prodottoOriginale.setCategoria(modificaDto.getCategoria());
        }

        if (modificaDto.getPrezzo() != null) {
            log.debug("Modifica prezzo prodotto ID {}: {} -> {}",
                    prodottoId, prodottoOriginale.getPrezzo(), modificaDto.getPrezzo());
            prodottoOriginale.setPrezzo(modificaDto.getPrezzo());
        }

        // Salva le modifiche
        ProdottiEntity prodottoModificato = prodottiRepository.save(prodottoOriginale);
        log.info("Prodotto modificato con successo - ID: {}", prodottoId);

        // Crea il risultato dettagliato confrontando originale vs modificato
        return creaRisultatoModifica(prodottoClone, prodottoModificato, modificaDto);
    }

    private RisultatoModificaProdottoDTO creaRisultatoModifica(
            ProdottiEntity prodottoOriginale,
            ProdottiEntity prodottoModificato,
            ModificaProdottoRequestDTO modificaDto) {

        List<String> campiModificati = new ArrayList<>();

        // Controllo modifica NOME
        if (modificaDto.getNome() != null &&
                !modificaDto.getNome().equals(prodottoOriginale.getNome())) {
            campiModificati.add("nome");
        }

        // Controllo modifica CATEGORIA
        if (modificaDto.getCategoria() != null &&
                !modificaDto.getCategoria().equals(prodottoOriginale.getCategoria())) {
            campiModificati.add("categoria");
        }

        // Controllo modifica PREZZO
        if (modificaDto.getPrezzo() != null &&
                (prodottoOriginale.getPrezzo() == null ||
                        modificaDto.getPrezzo().compareTo(prodottoOriginale.getPrezzo()) != 0)) {
            campiModificati.add("prezzo");
        }

        // Costruzione del messaggio dinamico
        String messaggio;
        if (campiModificati.isEmpty()) {
            messaggio = "Nessuna modifica applicata - i valori forniti sono identici a quelli esistenti";
        } else if (campiModificati.size() == 1) {
            messaggio = "Campo modificato: " + campiModificati.get(0);
        } else {
            messaggio = "Campi modificati: " + String.join(", ", campiModificati);
        }

        // Operazione sempre completa per le modifiche prodotto (a differenza degli ordini)
        boolean operazioneCompletata = true;

        log.info("Risultato modifica prodotto ID {}: {}", prodottoModificato.getId(), messaggio);

        return new RisultatoModificaProdottoDTO(
                prodottiMapper.prodottiEntityToProdottiDto(prodottoModificato),
                campiModificati,
                operazioneCompletata,
                messaggio
        );
    }
}
