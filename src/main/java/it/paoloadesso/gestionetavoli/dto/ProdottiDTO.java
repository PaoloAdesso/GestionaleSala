package it.paoloadesso.gestionetavoli.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProdottiDTO {

    @NotNull
    private Long idProdotto;

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    @NotNull
    private BigDecimal prezzo;

    public ProdottiDTO(Long idProdotto, String nome, String categoria, BigDecimal prezzo) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
    }

    public ProdottiDTO() {
    }

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
}
