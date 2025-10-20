package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoOrdine;

import java.time.LocalDate;
import java.util.List;

public class ListaOrdiniEProdottiByTavoloResponseDTO {

    private Long idOrdine;
    private Long idTavolo;
    private LocalDate dataOrdine;
    private StatoOrdine statoOrdine;

    private List<ProdottiOrdinatiResponseDTO> listaOrdineERelativiProdotti;

    public ListaOrdiniEProdottiByTavoloResponseDTO(Long idOrdine, Long idTavolo, LocalDate dataOrdine, StatoOrdine statoOrdine, List<ProdottiOrdinatiResponseDTO> listaOrdineERelativiProdotti) {
        this.idOrdine = idOrdine;
        this.idTavolo = idTavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
        this.listaOrdineERelativiProdotti = listaOrdineERelativiProdotti;
    }

    public ListaOrdiniEProdottiByTavoloResponseDTO() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }
    public List<ProdottiOrdinatiResponseDTO> getListaOrdineERelativiProdotti() {
        return listaOrdineERelativiProdotti;
    }

    public void setListaOrdineERelativiProdotti(List<ProdottiOrdinatiResponseDTO> listaOrdineERelativiProdotti) {
        this.listaOrdineERelativiProdotti = listaOrdineERelativiProdotti;
    }
}
