package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.CreaProdottiDTO;
import it.paoloadesso.gestionalesala.dto.ProdottiDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.mapper.ProdottiMapper;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
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

import java.util.List;

@Service
public class ProdottiService {

    private static final Logger log = LoggerFactory.getLogger(ProdottiService.class);
    private final ProdottiRepository prodottiRepository;
    private final ProdottiMapper prodottiMapper;
    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final OrdiniRepository ordiniRepository;

    public ProdottiService(ProdottiRepository prodottiRepository, ProdottiMapper prodottiMapper, OrdiniProdottiRepository ordiniProdottiRepository, OrdiniRepository ordiniRepository) {
        this.prodottiRepository = prodottiRepository;
        this.prodottiMapper = prodottiMapper;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.ordiniRepository = ordiniRepository;
    }

    @Transactional
    public ProdottiDTO creaProdotto(CreaProdottiDTO dto) {
        //controllo se il prodotto esiste
        if (prodottiRepository.existsByNomeIgnoreCase(dto.getNome())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il prodotto «" + dto.getNome() + "» è già presente");
        }

        ProdottiEntity prodotto = prodottiRepository.save(prodottiMapper.createProdottiDtoToEntity(dto));
        return prodottiMapper.prodottiEntityToDto(prodotto);
    }

    public List<ProdottiDTO> getAllProdotti() {
        List<ProdottiEntity> entities = prodottiRepository.findAll();

        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
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
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDTO> getAllProdottiEliminati() {
        List<ProdottiEntity> entities = prodottiRepository.findAllProdottiEliminati();
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
                .toList();
    }

    public List<ProdottiDTO> getProdottiByContainingCategoria(String nomeCategoria) {
        if (nomeCategoria == null || nomeCategoria.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Il nome della categoria non può essere vuoto.");
        }

        List<ProdottiEntity> entities = prodottiRepository.findByCategoriaContainingIgnoreCase(nomeCategoria.trim());
        return entities.stream()
                .map(prodottiMapper::prodottiEntityToDto)
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

    public ProdottiDTO ripristinaSingoloProdotto(Long idProdotto) {
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

        return prodottiMapper.prodottiEntityToDto(prodottoRipristinato);
    }
}
