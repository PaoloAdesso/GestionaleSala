package it.paoloadesso.gestionalesala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProdottiConDettaglioDeleteDTO {

    @NotNull
    private Long idProdotto;

    @NotBlank
    private String nome;

    @NotBlank
    private String categoria;

    @NotNull
    private BigDecimal prezzo;

    @NotNull
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    public ProdottiConDettaglioDeleteDTO(Long idProdotto, String nome, String categoria, BigDecimal prezzo, Boolean deleted, LocalDateTime deletedAt) {
        this.idProdotto = idProdotto;
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public ProdottiConDettaglioDeleteDTO() {
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
}
