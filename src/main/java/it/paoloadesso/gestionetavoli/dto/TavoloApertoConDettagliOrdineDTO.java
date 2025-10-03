package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.util.List;

public class TavoloApertoConDettagliOrdineDTO {
    private Long tavoloId;
    private String numeroNomeTavolo;
    private List<OrdineConProdottiDTO> ordini;
    private BigDecimal totaleComplessivoTavolo;

    public TavoloApertoConDettagliOrdineDTO(Long tavoloId, String numeroNomeTavolo, List<OrdineConProdottiDTO> ordini, BigDecimal totaleComplessivoTavolo) {
        this.tavoloId = tavoloId;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.ordini = ordini;
        this.totaleComplessivoTavolo = totaleComplessivoTavolo;
    }

    public TavoloApertoConDettagliOrdineDTO() {
    }

    public Long getTavoloId() {
        return tavoloId;
    }

    public void setTavoloId(Long tavoloId) {
        this.tavoloId = tavoloId;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public List<OrdineConProdottiDTO> getOrdini() {
        return ordini;
    }

    public void setOrdini(List<OrdineConProdottiDTO> ordini) {
        this.ordini = ordini;
    }

    public BigDecimal getTotaleComplessivoTavolo() {
        return totaleComplessivoTavolo;
    }

    public void setTotaleComplessivoTavolo(BigDecimal totaleComplessivoTavolo) {
        this.totaleComplessivoTavolo = totaleComplessivoTavolo;
    }
}
