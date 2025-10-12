package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoOrdine;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoRisultatoDto {
    private Long idOrdine;
    private int quantitaProdottiPagati;
    private int totalePezziPagati;
    private BigDecimal totalePagato;
    private LocalDateTime timestampPagamento;
    private StatoOrdine statoOrdine;

    public PagamentoRisultatoDto(Long idOrdine, int quantitaProdottiPagati, int totalePezziPagati, BigDecimal totalePagato, LocalDateTime timestampPagamento, StatoOrdine statoOrdine) {
        this.idOrdine = idOrdine;
        this.quantitaProdottiPagati = quantitaProdottiPagati;
        this.totalePezziPagati = totalePezziPagati;
        this.totalePagato = totalePagato;
        this.timestampPagamento = timestampPagamento;
        this.statoOrdine = statoOrdine;
    }

    public PagamentoRisultatoDto() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getQuantitaProdottiPagati() {
        return quantitaProdottiPagati;
    }

    public void setQuantitaProdottiPagati(int quantitaProdottiPagati) {
        this.quantitaProdottiPagati = quantitaProdottiPagati;
    }

    public int getTotalePezziPagati() {
        return totalePezziPagati;
    }

    public void setTotalePezziPagati(int totalePezziPagati) {
        this.totalePezziPagati = totalePezziPagati;
    }

    public BigDecimal getTotalePagato() {
        return totalePagato;
    }

    public void setTotalePagato(BigDecimal totalePagato) {
        this.totalePagato = totalePagato;
    }

    public LocalDateTime getTimestampPagamento() {
        return timestampPagamento;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }

    public void setTimestampPagamento(LocalDateTime timestampPagamento) {
        this.timestampPagamento = timestampPagamento;
    }
}
