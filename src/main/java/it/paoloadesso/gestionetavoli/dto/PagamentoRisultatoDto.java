package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagamentoRisultatoDto {
    private Long idOrdine;
    private int prodottiPagati;
    private BigDecimal totalePagato;
    private LocalDateTime timestampPagamento;

    public PagamentoRisultatoDto(Long idOrdine, int prodottiPagati, BigDecimal totalePagato, LocalDateTime timestampPagamento) {
        this.idOrdine = idOrdine;
        this.prodottiPagati = prodottiPagati;
        this.totalePagato = totalePagato;
        this.timestampPagamento = timestampPagamento;
    }

    public PagamentoRisultatoDto() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getProdottiPagati() {
        return prodottiPagati;
    }

    public void setProdottiPagati(int prodottiPagati) {
        this.prodottiPagati = prodottiPagati;
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

    public void setTimestampPagamento(LocalDateTime timestampPagamento) {
        this.timestampPagamento = timestampPagamento;
    }
}
