package it.paoloadesso.gestionalesala.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreaOrdiniDTO {
    @NotNull
    private Long idTavolo;

    @NotEmpty
    private List<ProdottiOrdinatiRequestDTO> listaProdottiOrdinati;

    public CreaOrdiniDTO() {
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public List<ProdottiOrdinatiRequestDTO> getListaProdottiOrdinati() {
        return listaProdottiOrdinati;
    }

    public void setListaProdottiOrdinati(List<ProdottiOrdinatiRequestDTO> listaProdottiOrdinati) {
        this.listaProdottiOrdinati = listaProdottiOrdinati;
    }

    public CreaOrdiniDTO(Long idTavolo, List<ProdottiOrdinatiRequestDTO> listaProdottiOrdinati) {
        this.idTavolo = idTavolo;
        this.listaProdottiOrdinati = listaProdottiOrdinati;
    }
}
