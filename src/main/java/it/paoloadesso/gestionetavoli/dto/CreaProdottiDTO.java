package it.paoloadesso.gestionetavoli.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreaProdottiDTO {

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal prezzo;

    public CreaProdottiDTO(String nome, String categoria, BigDecimal prezzo) {
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
    }

    public CreaProdottiDTO() {
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
