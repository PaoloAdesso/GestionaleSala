package it.paoloadesso.gestionalesala.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class CreaProdottiDTO {

    @NotBlank(message = "Il nome del prodotto è obbligatorio")
    @Size(min = 1, max = 100, message = "Il nome deve essere tra 1 e 100 caratteri")
    private String nome;

    @NotBlank(message = "La categoria è obbligatoria")
    @Size(min = 1, max = 50, message = "La categoria deve essere tra 1 e 50 caratteri")
    private String categoria;

    @NotNull(message = "Il prezzo è obbligatorio")
    @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
    @Digits(integer = 6, fraction = 2, message = "Prezzo non valido")
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

    @Override
    public String toString() {
        return "CreaProdottiDTO{" +
                "nome='" + nome + '\'' +
                ", categoria='" + categoria + '\'' +
                ", prezzo=" + prezzo +
                '}';
    }
}
