package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AnnullaPagamentoRisultatoDto {
    private Long idOrdine;
    private int prodottiNonPagati;
    private BigDecimal totalePagamentiAnnullati;
    private LocalDateTime timestampAnnullamentoPagamento;

    public AnnullaPagamentoRisultatoDto(Long idOrdine, int prodottiNonPagati, BigDecimal totalePagamentiAnnullati, LocalDateTime timestampAnnullamentoPagamento) {
        this.idOrdine = idOrdine;
        this.prodottiNonPagati = prodottiNonPagati;
        this.totalePagamentiAnnullati = totalePagamentiAnnullati;
        this.timestampAnnullamentoPagamento = timestampAnnullamentoPagamento;
    }

    public AnnullaPagamentoRisultatoDto() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getProdottiNonPagati() {
        return prodottiNonPagati;
    }

    public void setProdottiNonPagati(int prodottiNonPagati) {
        this.prodottiNonPagati = prodottiNonPagati;
    }

    public BigDecimal getTotalePagamentiAnnullati() {
        return totalePagamentiAnnullati;
    }

    public void setTotalePagamentiAnnullati(BigDecimal totalePagamentiAnnullati) {
        this.totalePagamentiAnnullati = totalePagamentiAnnullati;
    }

    public LocalDateTime getTimestampAnnullamentoPagamento() {
        return timestampAnnullamentoPagamento;
    }

    public void setTimestampAnnullamentoPagamento(LocalDateTime timestampAnnullamentoPagamento) {
        this.timestampAnnullamentoPagamento = timestampAnnullamentoPagamento;
    }
}
