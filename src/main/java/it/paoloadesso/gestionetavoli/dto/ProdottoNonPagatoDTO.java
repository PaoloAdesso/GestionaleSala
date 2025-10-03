package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;

public class ProdottoNonPagatoDTO {
    private Long prodottoId;
    private String nomeProdotto;
    private BigDecimal prezzo;
    private Integer quantita;
    private BigDecimal subtotale; // prezzo × quantità

    public ProdottoNonPagatoDTO(Long prodottoId, String nomeProdotto, BigDecimal prezzo, Integer quantita, BigDecimal subtotale) {
        this.prodottoId = prodottoId;
        this.nomeProdotto = nomeProdotto;
        this.prezzo = prezzo;
        this.quantita = quantita;
        this.subtotale = subtotale;
    }

    public ProdottoNonPagatoDTO() {
    }

    public Long getProdottoId() {
        return prodottoId;
    }

    public void setProdottoId(Long prodottoId) {
        this.prodottoId = prodottoId;
    }

    public String getNomeProdotto() {
        return nomeProdotto;
    }

    public void setNomeProdotto(String nomeProdotto) {
        this.nomeProdotto = nomeProdotto;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }

    public Integer getQuantita() {
        return quantita;
    }

    public void setQuantita(Integer quantita) {
        this.quantita = quantita;
    }

    public BigDecimal getSubtotale() {
        return subtotale;
    }

    public void setSubtotale(BigDecimal subtotale) {
        this.subtotale = subtotale;
    }
}
