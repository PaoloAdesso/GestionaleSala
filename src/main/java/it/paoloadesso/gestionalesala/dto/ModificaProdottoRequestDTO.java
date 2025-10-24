package it.paoloadesso.gestionalesala.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ModificaProdottoRequestDTO {

    @Size(min = 1, max = 100, message = "Il nome deve essere tra 1 e 100 caratteri")
    private String nome;

    @Size(min = 1, max = 50, message = "La categoria deve essere tra 1 e 50 caratteri")
    private String categoria;

    @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
    @Digits(integer = 6, fraction = 2, message = "Prezzo non valido")
    private BigDecimal prezzo;

    public ModificaProdottoRequestDTO() {
    }

    public ModificaProdottoRequestDTO(String nome, String categoria, BigDecimal prezzo) {
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
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

    @JsonIgnore
    public boolean isEmpty() {
        return nome == null && categoria == null && prezzo == null;
    }
}
