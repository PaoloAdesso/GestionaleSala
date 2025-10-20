package it.paoloadesso.gestionetavoli.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ProdottiOrdinatiRequestDTO {

    @NotNull
    private Long idProdotto;

    @NotNull
    @Positive
    private Integer quantitaProdotto;

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public Integer getQuantitaProdotto() {
        return quantitaProdotto;
    }

    public void setQuantitaProdotto(Integer quantitaProdotto) {
        this.quantitaProdotto = quantitaProdotto;
    }

    public ProdottiOrdinatiRequestDTO() {
    }

    public ProdottiOrdinatiRequestDTO(Long idProdotto, Integer quantitaProdotto) {
        this.idProdotto = idProdotto;
        this.quantitaProdotto = quantitaProdotto;
    }
}
