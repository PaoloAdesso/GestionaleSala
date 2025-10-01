package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TavoloApertoDTO {
    private Long tavoloId;
    private String numeroNomeTavolo;
    private Long ordineId;
    private BigDecimal totaleNonPagato;
    private Integer numeroProdottiNonPagati;
    private LocalDateTime oraApertura;

    public TavoloApertoDTO() {}

    public TavoloApertoDTO(Long tavoloId, String numeroNomeTavolo, Long ordineId,
                           BigDecimal totaleNonPagato, Integer numeroProdottiNonPagati,
                           LocalDateTime oraApertura) {
        this.tavoloId = tavoloId;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.ordineId = ordineId;
        this.totaleNonPagato = totaleNonPagato;
        this.numeroProdottiNonPagati = numeroProdottiNonPagati;
        this.oraApertura = oraApertura;
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

    public Long getOrdineId() {
        return ordineId;
    }

    public void setOrdineId(Long ordineId) {
        this.ordineId = ordineId;
    }

    public BigDecimal getTotaleNonPagato() {
        return totaleNonPagato;
    }

    public void setTotaleNonPagato(BigDecimal totaleNonPagato) {
        this.totaleNonPagato = totaleNonPagato;
    }

    public Integer getNumeroProdottiNonPagati() {
        return numeroProdottiNonPagati;
    }

    public void setNumeroProdottiNonPagati(Integer numeroProdottiNonPagati) {
        this.numeroProdottiNonPagati = numeroProdottiNonPagati;
    }

    public LocalDateTime getOraApertura() {
        return oraApertura;
    }

    public void setOraApertura(LocalDateTime oraApertura) {
        this.oraApertura = oraApertura;
    }
}
