package it.paoloadesso.gestionetavoli.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdottiDaRimuovereDTO {

    @NotNull
    @Positive
    private Long idProdotto;

    @NotNull
    @Positive
    private Integer quantitaDaRimuovere;

    public ProdottiDaRimuovereDTO() {}

    public ProdottiDaRimuovereDTO(Long idProdotto, Integer quantitaDaRimuovere) {
        this.idProdotto = idProdotto;
        this.quantitaDaRimuovere = quantitaDaRimuovere;
    }

    public Long getIdProdotto() { return idProdotto; }
    public void setIdProdotto(Long idProdotto) { this.idProdotto = idProdotto; }

    public Integer getQuantitaDaRimuovere() { return quantitaDaRimuovere; }
    public void setQuantitaDaRimuovere(Integer quantitaDaRimuovere) { this.quantitaDaRimuovere = quantitaDaRimuovere; }

    @Override
    public String toString() {
        return "ProdottiDaRimuovereDto{" +
                "idProdotto=" + idProdotto +
                ", quantitaDaRimuovere=" + quantitaDaRimuovere +
                '}';
    }
}
