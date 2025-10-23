package it.paoloadesso.gestionalesala.services;

import it.paoloadesso.gestionalesala.dto.*;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import it.paoloadesso.gestionalesala.enums.StatoPagato;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import it.paoloadesso.gestionalesala.mapper.OrdiniMapper;
import it.paoloadesso.gestionalesala.repositories.OrdiniProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.OrdiniRepository;
import it.paoloadesso.gestionalesala.repositories.ProdottiRepository;
import it.paoloadesso.gestionalesala.repositories.TavoliRepository;
import it.paoloadesso.gestionalesala.utils.DataLavorativaUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrdiniService {

    private static final Logger log = LoggerFactory.getLogger(OrdiniService.class);

    private final OrdiniRepository ordiniRepository;
    private final OrdiniProdottiRepository ordiniProdottiRepository;
    private final TavoliRepository tavoliRepository;
    private final ProdottiRepository prodottiRepository;

    private final OrdiniMapper ordiniMapper;

    private final DataLavorativaUtil dataLavorativaUtil;



    public OrdiniService(OrdiniRepository ordiniRepository, OrdiniProdottiRepository ordiniProdottiRepository,
                         TavoliRepository tavoliRepository, ProdottiRepository prodottiRepository, OrdiniMapper ordiniMapper, DataLavorativaUtil dataLavorativaUtil) {
        this.ordiniRepository = ordiniRepository;
        this.ordiniProdottiRepository = ordiniProdottiRepository;
        this.tavoliRepository = tavoliRepository;
        this.prodottiRepository = prodottiRepository;
        this.ordiniMapper = ordiniMapper;
        this.dataLavorativaUtil = dataLavorativaUtil;
    }

    /**
     * Questo metodo crea un nuovo ordine per un tavolo.
     * Prima controllo che il tavolo esista, poi creo l'ordine base e salvo subito per avere l'ID.
     * Dopo aggiungo tutti i prodotti richiesti creando le relazioni nella tabella ponte ordini_prodotti.
     * Uso @Transactional perché se qualcosa va storto devo annullare tutto (ordine + prodotti).
     */
    @Transactional
    public OrdiniDTO creaOrdine(CreaOrdiniDTO dto) {
        // Prima cosa: controllo che il tavolo esista davvero nel database
        controlloSeIlTavoloEsiste(dto.getIdTavolo());

        // Carico l'entity completa del tavolo dal database perché mi servirà dopo
        TavoliEntity tavolo = tavoliRepository.findById(dto.getIdTavolo())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato"));

        // Creo l'ordine base (senza prodotti ancora) e lo salvo subito per avere l'ID generato
        OrdiniEntity ordine = new OrdiniEntity();
        ordine.setTavolo(tavolo);
        ordine.setDataOrdine(oggiLavorativo());
        ordine = ordiniRepository.save(ordine);// Ora ho l'ID generato automaticamente dal database

        // Creo una lista per contenere tutte le relazioni ordine-prodotto
        List<OrdiniProdottiEntity> ordiniProdottiEntities = new ArrayList<>();

        // Per ogni prodotto che l'utente vuole ordinare
        for (ProdottiOrdinatiRequestDTO prodottoDto : dto.getListaProdottiOrdinati()) {
            // Controllo che il prodotto esista nel database
            ProdottiEntity prodotto = prodottiRepository.findById(prodottoDto.getIdProdotto())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Prodotto con ID " + prodottoDto.getIdProdotto() + " non trovato"));

            // Creo la chiave composita per la tabella ponte ordini_prodotti
            // (perché la tabella ha una chiave primaria composta da idOrdine + idProdotto)
            OrdiniProdottiId id = new OrdiniProdottiId(ordine.getIdOrdine(), prodotto.getId());

            // Creo l'entity che rappresenta la relazione tra questo ordine e questo prodotto
            OrdiniProdottiEntity ordineProdotti = new OrdiniProdottiEntity();
            ordineProdotti.setId(id);
            ordineProdotti.setOrdine(ordine);
            ordineProdotti.setProdotto(prodotto);
            ordineProdotti.setQuantitaProdotto(prodottoDto.getQuantitaProdotto());

            // Aggiungo questa relazione alla lista
            ordiniProdottiEntities.add(ordineProdotti);
        }

        // Salvo tutte le relazioni ordine-prodotto in una volta sola per essere più efficiente
        ordiniProdottiRepository.saveAll(ordiniProdottiEntities);
        // Cambio lo stato del tavolo in OCCUPATO
        if (tavolo.getStatoTavolo() != StatoTavolo.OCCUPATO) {
            tavolo.setStatoTavolo(StatoTavolo.OCCUPATO);
        }
        // Alternativa commentata: potrei usare MapStruct invece di fare la conversione manualmente
        // return ordiniMapper.ordiniEntityToDto(ordine)

        // Creo il DTO di risposta manualmente con i dati dell'ordine appena creato
        OrdiniDTO ordineDto = new OrdiniDTO();
        ordineDto.setIdOrdine(ordine.getIdOrdine());
        ordineDto.setIdTavolo(tavolo.getId());
        ordineDto.setDataOrdine(ordine.getDataOrdine());
        ordineDto.setStatoOrdine(ordine.getStatoOrdine());

        return ordineDto;
    }

    /**
     * Questo metodo chiude un ordine cambiando il suo stato da APERTO a CHIUSO.
     * Controllo prima che tutti i prodotti siano stati pagati, perché non posso
     * chiudere un ordine se ci sono ancora cose da pagare.
     * Uso @Transactional perché modifico il database.
     */
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
        // Cambio lo stato dell'ordine a CHIUSO e lo salvo nel db
        ordine.setStatoOrdine(StatoOrdine.CHIUSO);
        ordiniRepository.save(ordine);

        // Trovo il tavolo
        TavoliEntity tavolo = ordine.getTavolo();
        // Verifico che non ci siano altri ordini aperti
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

    public List<OrdiniDTO> getListaTuttiOrdiniAperti() {
        // Cerco tutti gli ordini che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByStatoOrdineNot(StatoOrdine.CHIUSO);
        // Converto ogni Entity in DTO
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    public List<OrdiniDTO> getListaOrdiniApertiByTavolo(Long idTavolo) {
        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);

        // Cerco tutti gli ordini di questo tavolo che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByTavoloIdAndStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        // Converto ogni Entity in DTO usando il mapper e ritorno la lista
        return ordini.stream()
                .map(el -> ordiniMapper.ordiniEntityToDto(el))
                .collect(Collectors.toList());
    }

    public List<OrdiniDTO> getOrdiniDiOggi() {
        // Cerco ordini con data di oggi che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository.findByDataOrdineAndStatoOrdineNot(oggiLavorativo(), StatoOrdine.CHIUSO);
        // Uso method reference per convertire Entity in DTO
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    public List<OrdiniDTO> getOrdiniOggiByTavolo(@NotNull @Positive Long idTavolo) {
        // Prima controllo che il tavolo esista
        controlloSeIlTavoloEsiste(idTavolo);
        // Cerco ordini di questo tavolo, di oggi, che NON sono chiusi
        List<OrdiniEntity> ordini = ordiniRepository
                .findByTavoloIdAndDataOrdineAndStatoOrdineNot(idTavolo, oggiLavorativo(), StatoOrdine.CHIUSO);
        return ordini.stream().map(ordiniMapper::ordiniEntityToDto).toList();
    }

    /**
     * Trova gli ordini con TUTTI i loro prodotti per un tavolo specifico.
     * Oltre ai dati dell'ordine restituisce anche QUALI prodotti ci sono in ogni ordine.
     */
    public List<ListaOrdiniEProdottiByTavoloResponseDTO> getDettaglioOrdineByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineStatoOrdineNot(idTavolo, StatoOrdine.CHIUSO);

        // Uso il metodo di aiuto per costruire la risposta
        return costruzioneDettagliOrdine(righe);
    }

    public List<ListaOrdiniEProdottiByTavoloResponseDTO> getDettaglioOrdineDiOggiByIdTavolo(
            @NotNull @Positive Long idTavolo) {

        controlloSeIlTavoloEsiste(idTavolo);

        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(idTavolo, oggiLavorativo(), StatoOrdine.CHIUSO);

        return costruzioneDettagliOrdine(righe);
    }

    /**
     * Questo metodo modifica un ordine esistente aggiungendo nuovi prodotti o cambiando tavolo.
     * Uso @Transactional perché faccio operazioni multiple che devono avere successo tutte insieme.
     * Se qualcosa va storto, il database torna allo stato precedente automaticamente.
     * <p>
     * Gestisco anche i "successi parziali": se alcuni prodotti vengono aggiunti ma altri danno errore,
     * salvo quelli buoni e notifico gli errori nel DTO di risposta.
     */
    @Transactional
    public RisultatoModificaOrdineDTO modificaOrdine(
            Long idOrdine, @Valid ModificaOrdineRequestDTO requestDto) {
        // Prima controllo: la richiesta deve contenere almeno una modifica
        if (requestDto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La richiesta di modifica è vuota.");
        }

        // Controllo se l'ordine esiste nel database
        OrdiniEntity ordine = ordiniRepository.findById(idOrdine)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Ordine con ID «" + idOrdine + "» non trovato."));

        // Controllo business: non posso modificare un ordine già chiuso
        if (ordine.getStatoOrdine() == StatoOrdine.CHIUSO) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "L'ordine è chiuso, non è possibile modificarlo.");
        }

        // Se l'utente vuole cambiare tavolo (questa operazione o riesce o fallisce completamente)
        if (requestDto.getNuovoIdTavolo() != null) {
            // Prima controllo che il nuovo tavolo esista
            controlloSeIlTavoloEsiste(requestDto.getNuovoIdTavolo());

            // Carico l'entity completa del nuovo tavolo
            TavoliEntity nuovoTavolo = tavoliRepository.findById(requestDto.getNuovoIdTavolo())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Tavolo con ID «" + requestDto.getNuovoIdTavolo() + "» non trovato."));

            // Cambio il tavolo nell'ordine (in memoria per ora)
            ordine.setTavolo(nuovoTavolo);
        }

        // Variabili per tracciare successi e fallimenti nell'aggiunta prodotti
        List<String> errori = new ArrayList<>();
        int prodottiAggiunti = 0;
        int prodottiRimossi = 0;

        // Se l'utente vuole rimuovere dei prodotti
        if (requestDto.getProdottiDaRimuovere() != null && !requestDto.getProdottiDaRimuovere().isEmpty()) {

            // Per ogni prodotto da rimuovere, provo a processarlo singolarmente
            for (ProdottiDaRimuovereDTO prodotto : requestDto.getProdottiDaRimuovere()) {
                try {
                    // Provo a rimuovere questo singolo prodotto
                    boolean rimosso = processaRimozioneSingoloProdotto(idOrdine, prodotto);
                    if (rimosso) {
                        prodottiRimossi++;
                    }

                } catch (ResponseStatusException e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Rimozione Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getReason();
                    errori.add(messaggioErrore);

                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Rimozione Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                }
            }
        }

        // Se l'utente vuole aggiungere dei prodotti (qui gestisco errori parziali)
        if (requestDto.getProdottiDaAggiungere() != null && !requestDto.getProdottiDaAggiungere().isEmpty()) {

            // Per ogni prodotto da aggiungere, provo a processarlo singolarmente
            for (ProdottiOrdinatiRequestDTO prodotto : requestDto.getProdottiDaAggiungere()) {
                try {
                    // Provo ad aggiungere questo singolo prodotto
                    boolean aggiunto = processaSingoloProdotto(idOrdine, ordine, prodotto);
                    if (aggiunto) {
                        prodottiAggiunti++;
                    }
                } catch (ResponseStatusException e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Aggiunta Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getReason();
                    errori.add(messaggioErrore);

                } catch (Exception e) {
                    // Se questo prodotto da errore, lo segno ma continuo con gli altri
                    String messaggioErrore = "Aggiunta Prodotto ID " + prodotto.getIdProdotto() +
                            ": " + e.getMessage();
                    errori.add(messaggioErrore);
                }
            }
        }

        // Salvo sempre le modifiche dell'ordine (cambio tavolo, prodotti aggiunti con successo)
        ordiniRepository.save(ordine);

        // Ricarico tutti i prodotti dell'ordine per la risposta aggiornata
        List<OrdiniProdottiEntity> righe = ordiniProdottiRepository
                .findByOrdineIdOrdine(idOrdine);

        ListaOrdiniEProdottiByTavoloResponseDTO ordineAggiornato = costruzioneDettagliOrdine(righe).get(0);

        // Creo il risultato con informazioni complete su successi e fallimenti
        return creaRisultatoModifica(ordineAggiornato, prodottiAggiunti, prodottiRimossi, errori, requestDto);
    }

    @Transactional
    public void eliminaOrdine(Long idOrdine) {
        boolean esisteGiaCancellato = ordiniRepository.existsDeletedOrdine(idOrdine);

        if (esisteGiaCancellato) {
            throw new IllegalStateException("Ordine già cancellato: " + idOrdine);
        }

        OrdiniEntity ordine = ordiniRepository.findById(idOrdine)
                .orElseThrow(() -> new EntityNotFoundException("Ordine non trovato: " + idOrdine));

        List<OrdiniProdottiEntity> prodottiOrdine = ordiniProdottiRepository.findByOrdineIdOrdine(idOrdine);

        if (!prodottiOrdine.isEmpty()) {
            ordiniProdottiRepository.deleteAll(prodottiOrdine);
            log.info("Eliminati {} prodotti dell'ordine {}", prodottiOrdine.size(), idOrdine);
        }
            ordiniRepository.delete(ordine);

        log.info("Ordine {} cancellato (soft delete)", idOrdine);
    }

    @Transactional
    public void eliminaFisicamenteOrdine(Long idOrdine) {
        OrdiniEntity ordine = ordiniRepository.findByIdOrdine(idOrdine);

        if (ordine == null) {
            throw new EntityNotFoundException("Nessun ordine con ID " + idOrdine + " presente");
        }

        ordiniRepository.deletePhysically(idOrdine);
        log.info("Ordine con ID {} cancellato definitivamente (hard delete)", idOrdine);
    }

    public List<OrdiniDTO> getAllOrdiniEliminati() {
        List<OrdiniEntity> entities = ordiniRepository.findAllOrdiniEliminati();
        return entities.stream()
                .map(ordiniMapper::ordiniEntityToDto)
                .toList();
    }

    @Transactional
    public OrdiniDTO ripristinaOrdine(Long idOrdine) {
        log.debug("Tentativo ripristino ordine con ID: {}", idOrdine);

        OrdiniEntity ordine = ordiniRepository.findDeletedOrdineById(idOrdine)
                .orElseThrow(() -> new EntityNotFoundException("Ordine cancellato non trovato con ID: " + idOrdine));

        TavoliEntity tavolo = ordine.getTavolo();
        if (tavolo == null) {
            throw new IllegalStateException("Ordine senza tavolo: impossibile ripristinare");
        }

        if (tavolo.getDeleted()) {
            throw new IllegalStateException("Il tavolo collegato è stato rimosso: ripristino non consentito");
        }

        ordine.setDeleted(false);
        ordine.setDeletedAt(null);
        OrdiniEntity ordineRipristinato = ordiniRepository.save(ordine);

        if (ordine.getStatoOrdine() != StatoOrdine.CHIUSO && tavolo.getStatoTavolo() != StatoTavolo.OCCUPATO) {
            tavolo.setStatoTavolo(StatoTavolo.OCCUPATO);
            tavoliRepository.save(tavolo);
        }

        log.info("Ordine {} ripristinato con successo", idOrdine);
        return ordiniMapper.ordiniEntityToDto(ordineRipristinato);
    }


    private void controlloSeIlTavoloEsiste(Long idTavolo) {
        if (idTavolo == null || !tavoliRepository.existsById(idTavolo)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato");
        }
    }

    /**
     * Metodo di aiuto: trasforma una lista di righe ordini_prodotti in DTO completi.
     * Con questo riesco a raggruppare le righe per ordine e per ogni ordine creare un
     * DTO con la lista dei suoi prodotti.
     */
    private List<ListaOrdiniEProdottiByTavoloResponseDTO> costruzioneDettagliOrdine(List<OrdiniProdottiEntity> righe) {
        // Raggruppo tutte le righe per ID ordine
        // Risultato: una mappa dove la chiave è l'ID ordine e il valore è la lista delle sue righe
        Map<Long, List<OrdiniProdottiEntity>> byOrdine = righe.stream()
                .collect(Collectors.groupingBy(r -> r.getOrdine().getIdOrdine()));

        // Per ogni gruppo di righe (che rappresenta un ordine)
        return byOrdine.values().stream().map(righeOrdine -> {
            // Prendo la prima riga per avere i dati base dell'ordine
            // (tutte le righe hanno gli stessi dati ordine, cambiano solo i prodotti)
            ListaOrdiniEProdottiByTavoloResponseDTO dtoBase =
                    ordiniMapper.ordiniProdottiEntityToDto(righeOrdine.get(0));

            // Creo la lista dei prodotti scorrendo tutte le righe di questo ordine
            List<ProdottiOrdinatiResponseDTO> prodotti = righeOrdine.stream().map(e -> {
                // Per ogni riga creo un DTO prodotto
                ProdottiOrdinatiResponseDTO p = new ProdottiOrdinatiResponseDTO();
                p.setIdProdotto(e.getProdotto().getId());
                p.setQuantitaProdotto(e.getQuantitaProdotto());
                p.setStatoPagato(e.getStatoPagato());
                return p;
            }).toList();

            // Attacco la lista prodotti al DTO base dell'ordine
            dtoBase.setListaOrdineERelativiProdotti(prodotti);
            return dtoBase;
        }).toList();
    }

    /**
     * Metodo di aiuto: processa un singolo prodotto da aggiungere all'ordine.
     * Ritorna true se il prodotto è stato aggiunto/aggiornato con successo.
     * Lancia eccezioni specifiche se qualcosa va storto.
     */
    private boolean processaSingoloProdotto(Long idOrdine, OrdiniEntity ordine, ProdottiOrdinatiRequestDTO prodotto) {

        // Controllo che il prodotto esista nel database
        ProdottiEntity prodottoEntity = prodottiRepository.findById(prodotto.getIdProdotto())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prodotto con ID " + prodotto.getIdProdotto() + " non trovato nel menu"
                ));

        // Creo la chiave per controllare se il prodotto è già nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è già presente nell'ordine
        if (ordiniProdottiRepository.existsById(chiave)) {
            // CASO 1: Prodotto già presente - aggiorno la quantità sommando
            OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave).get();
            Integer quantitaEsistente = ordineEsistente.getQuantitaProdotto();
            Integer quantitaDaAggiungere = prodotto.getQuantitaProdotto();
            Integer quantitaModificataFinale = quantitaEsistente + quantitaDaAggiungere;

            ordineEsistente.setQuantitaProdotto(quantitaModificataFinale);
            ordiniProdottiRepository.save(ordineEsistente);

        } else {
            // CASO 2: Prodotto nuovo - creo una nuova relazione ordine-prodotto
            OrdiniProdottiEntity nuovaRelazione = new OrdiniProdottiEntity();
            nuovaRelazione.setId(chiave);
            nuovaRelazione.setOrdine(ordine);
            nuovaRelazione.setProdotto(prodottoEntity);
            nuovaRelazione.setQuantitaProdotto(prodotto.getQuantitaProdotto());

            ordiniProdottiRepository.save(nuovaRelazione);
        }

        return true;
    }

    /**
     * Metodo di aiuto: processa la rimozione di una quantità specifica di un prodotto dall'ordine.
     * Ritorna true se il prodotto è stato rimosso/aggiornato con successo.
     * Lancia eccezioni specifiche se qualcosa va storto.
     */
    private boolean processaRimozioneSingoloProdotto(Long idOrdine, ProdottiDaRimuovereDTO prodotto) {

        // Creo la chiave per trovare il prodotto nell'ordine
        OrdiniProdottiId chiave = new OrdiniProdottiId(idOrdine, prodotto.getIdProdotto());

        // Controllo se il prodotto è presente nell'ordine
        OrdiniProdottiEntity ordineEsistente = ordiniProdottiRepository.findById(chiave)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Prodotto con ID " + prodotto.getIdProdotto() + " non presente nell'ordine"
                ));

        int quantitaAttuale = ordineEsistente.getQuantitaProdotto();
        int quantitaDaRimuovere = prodotto.getQuantitaDaRimuovere();

        // Controllo che non stia provando a rimuovere più di quello che c'è
        if (quantitaDaRimuovere > quantitaAttuale) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantità da rimuovere (" + quantitaDaRimuovere +
                            ") maggiore di quella presente (" + quantitaAttuale + ")"
            );
        }

        int quantitaFinale = quantitaAttuale - quantitaDaRimuovere;

        if (quantitaFinale == 0) {
            // CASO 1: Rimuovo tutto - cancello la riga
            ordiniProdottiRepository.delete(ordineEsistente);
        } else {
            // CASO 2: Rimozione parziale - aggiorno la quantità
            ordineEsistente.setQuantitaProdotto(quantitaFinale);
            ordiniProdottiRepository.save(ordineEsistente);
        }

        return true;
    }

    /**
     * Metodo di aiuto: crea il DTO risultato con le informazioni complete
     * su cosa è andato bene e cosa è andato storto nella modifica.
     */
    private RisultatoModificaOrdineDTO creaRisultatoModifica(
            ListaOrdiniEProdottiByTavoloResponseDTO ordine,
            int prodottiAggiunti,
            int prodottiRimossi,
            List<String> errori,
            ModificaOrdineRequestDTO requestDto) {

        boolean operazioneCompleta = errori.isEmpty();
        String messaggio = costruisciMessaggio(requestDto, prodottiAggiunti, prodottiRimossi, errori, operazioneCompleta);

        return new RisultatoModificaOrdineDTO(ordine, prodottiAggiunti, prodottiRimossi, errori, operazioneCompleta, messaggio);
    }

    /**
     * Costruisce il messaggio dinamico in base alle operazioni effettuate
     */
    private String costruisciMessaggio(ModificaOrdineRequestDTO requestDto, int prodottiAggiunti,
                                       int prodottiRimossi, List<String> errori, boolean operazioneCompleta) {

        if (operazioneCompleta) {
            List<String> operazioni = new ArrayList<>();

            if (requestDto.getNuovoIdTavolo() != null) {
                operazioni.add("Tavolo cambiato");
            }

            if (prodottiAggiunti > 0) {
                String msg = (prodottiAggiunti == 1) ? "1 prodotto aggiunto" : prodottiAggiunti + " prodotti aggiunti";
                operazioni.add(msg);
            }

            if (prodottiRimossi > 0) {
                String msg = (prodottiRimossi == 1) ? "1 prodotto rimosso" : prodottiRimossi + " prodotti rimossi";
                operazioni.add(msg);
            }

            return String.join(" e ", operazioni) + " con successo";

        } else {
            List<String> successi = new ArrayList<>();

            if (prodottiAggiunti > 0) {
                String msg = (prodottiAggiunti == 1) ? "1 prodotto aggiunto" : prodottiAggiunti + " prodotti aggiunti";
                successi.add(msg);
            }

            if (prodottiRimossi > 0) {
                String msg = (prodottiRimossi == 1) ? "1 prodotto rimosso" : prodottiRimossi + " prodotti rimossi";
                successi.add(msg);
            }

            String erroriMsg = (errori.size() == 1) ? "1 con errore" : errori.size() + " con errori";

            if (successi.isEmpty()) {
                return "Nessuna operazione completata a causa " + ((errori.size() == 1) ? "dell'errore" : "degli errori");
            } else {
                return String.join(" e ", successi) + ", " + erroriMsg;
            }
        }
    }

    private LocalDate oggiLavorativo() {
        return dataLavorativaUtil.getDataLavorativa();
    }

}
