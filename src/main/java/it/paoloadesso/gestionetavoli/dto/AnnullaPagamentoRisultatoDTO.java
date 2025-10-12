package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AnnullaPagamentoRisultatoDTO {
    private Long idOrdine;
    private int quantitaProdottiNonPagati;
    private int totalePezziNonPagati;
    private BigDecimal totalePagamentiAnnullati;
    private LocalDateTime timestampAnnullamentoPagamento;

    public AnnullaPagamentoRisultatoDTO() {
    }

    public AnnullaPagamentoRisultatoDTO(Long idOrdine, int quantitaProdottiNonPagati, int totalePezziNonPagati, BigDecimal totalePagamentiAnnullati, LocalDateTime timestampAnnullamentoPagamento) {
        this.idOrdine = idOrdine;
        this.quantitaProdottiNonPagati = quantitaProdottiNonPagati;
        this.totalePezziNonPagati = totalePezziNonPagati;
        this.totalePagamentiAnnullati = totalePagamentiAnnullati;
        this.timestampAnnullamentoPagamento = timestampAnnullamentoPagamento;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public int getQuantitaProdottiNonPagati() {
        return quantitaProdottiNonPagati;
    }

    public void setQuantitaProdottiNonPagati(int quantitaProdottiNonPagati) {
        this.quantitaProdottiNonPagati = quantitaProdottiNonPagati;
    }

    public int getTotalePezziNonPagati() {
        return totalePezziNonPagati;
    }

    public void setTotalePezziNonPagati(int totalePezziNonPagati) {
        this.totalePezziNonPagati = totalePezziNonPagati;
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
