package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.exceptionhandling.ModificaVuotaException;
import it.paoloadesso.gestionalesala.exceptionhandling.ProdottoEliminatoException;
import it.paoloadesso.gestionalesala.exceptionhandling.ProdottoNotFoundException;
import it.paoloadesso.gestionalesala.mapper.ProdottiMapper;
import it.paoloadesso.gestionalesala.repositories.ProdottiRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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
        //controllo se il prodotto esiste
        if (prodottiRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il prodotto «" + dto.getNome() + "» è già presente");
        }

        ProdottiEntity prodotto = prodottiRepository.save(prodottiMapper.createProdottiDtoToEntity(dto));
        return prodottiMapper.prodottiEntityToProdottiDto(prodotto);
    }

    public List<ProdottiDTO> getAllProdotti() {
        List<ProdottiEntity> entities = prodottiRepository.findAll();

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<String> getAllCategorie() {
        return prodottiRepository.findAllCategorieDistinct();
    }

    /**
     * Questo metodo cerca prodotti il cui nome contiene la stringa passata.
     * Ad esempio: se cerco "pizza" trovo "Pizza Margherita", "Pizza 4 Formaggi", ecc.
     * La ricerca è case-insensitive (non fa differenza tra maiuscole e minuscole).
     */
    public List<ProdottiDTO> getProdottiByContainingNome(@NotBlank String nomeProdotto) {
        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome del prodotto non può essere vuoto.");
        }
        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCase(nomeProdotto.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<ProdottiConDettaglioDeleteDTO> getTuttiProdottiAttiviEdEliminatiByContainingNome(@NotBlank String nomeProdotto) {
        if (nomeProdotto == null || nomeProdotto.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome del prodotto non può essere vuoto.");
        }
        List<ProdottiEntity> entities = prodottiRepository.findByNomeContainingIgnoreCaseNative(nomeProdotto.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiConDettaglioDeleteDto)
                .toList();
    }

    public List<ProdottiDTO> getAllProdottiEliminati() {
        List<ProdottiEntity> entities = prodottiRepository.findAllProdottiEliminati();
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    public List<ProdottiDTO> getProdottiByContainingCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome della categoria non può essere vuoto.");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(nomeCategoria.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToProdottiDto)
                .toList();
    }

    @Transactional
    public void deleteProdotto(@Positive Long idProdotto) {
        boolean esisteGiaCancellato = prodottiRepository.existsDeletedProdotto(idProdotto);

        if (esisteGiaCancellato) {
            throw new IllegalStateException("Prodotto con ID " + idProdotto + " già cancellato");
        }

        ProdottiEntity prodotto = prodottiRepository.findById(idProdotto)
                .orElseThrow(() -> new EntityNotFoundException("Prodotto con ID " + idProdotto + " non trovato" ));

        prodottiRepository.delete(prodotto);

        log.info("Prodotto con ID {} cancellato (soft delete)", idProdotto);
    }

    public ProdottiConDettaglioDeleteDTO ripristinaSingoloProdotto(Long idProdotto) {
        ProdottiEntity prodottoDaRipristinare = prodottiRepository.findByIdInclusoEliminati(idProdotto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Prodotto con ID " + idProdotto + " non trovato."));

        if (prodottoDaRipristinare.getDeleted() == false) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Il prodotto con ID " + idProdotto + " «" + prodottoDaRipristinare.getNome() + "» non risulta eliminato," +
                            " pertanto non è necessario ripristinarlo.");
        }

        prodottoDaRipristinare.setDeleted(false);
        prodottoDaRipristinare.setDeletedAt(null);
        ProdottiEntity prodottoRipristinato = prodottiRepository.save(prodottoDaRipristinare);

        return prodottiMapper.prodottiEntityToProdottiConDettaglioDeleteDto(prodottoRipristinato);
    }

    @Transactional
    public RisultatoModificaProdottoDTO modificaProdotto(Long prodottoId, ModificaProdottoRequestDTO modificaDto) {

        if (modificaDto.isEmpty()) {
            throw new ModificaVuotaException();
        }

        // Salva lo stato originale PRIMA delle modifiche
        ProdottiEntity prodottoOriginale = prodottiRepository.findById(prodottoId)
                .orElseThrow(() -> new ProdottoNotFoundException(prodottoId));

        if (prodottoOriginale.getDeleted()) {
            throw new ProdottoEliminatoException(prodottoId);
        }

        // Clona l'entità originale per confronto (semplice approccio)
        ProdottiEntity prodottoClone = new ProdottiEntity();
        prodottoClone.setNome(prodottoOriginale.getNome());
        prodottoClone.setCategoria(prodottoOriginale.getCategoria());
        prodottoClone.setPrezzo(prodottoOriginale.getPrezzo());

        // Applica le modifiche
        if (modificaDto.getNome() != null) {
            prodottoOriginale.setNome(modificaDto.getNome());
        }

        if (modificaDto.getCategoria() != null) {
            prodottoOriginale.setCategoria(modificaDto.getCategoria());
        }

        if (modificaDto.getPrezzo() != null) {
            prodottoOriginale.setPrezzo(modificaDto.getPrezzo());
        }

        // Salva le modifiche
        ProdottiEntity prodottoModificato = prodottiRepository.save(prodottoOriginale);

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
        boolean operazioneCompleta = true;

        return new RisultatoModificaProdottoDTO(
                prodottiMapper.prodottiEntityToProdottiDto(prodottoModificato),
                campiModificati,
                operazioneCompleta,
                messaggio
        );
    }



}
