package it.paoloadesso.gestionalesala.dto;

import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TavoliConDettaglioDeleteDTO {

    @NotNull
    private Long id;

    @NotBlank
    private String numeroNomeTavolo;

    @NotNull
    private StatoTavolo statoTavolo;

    @NotNull
    private Boolean deleted = false;

    private LocalDateTime deletedAt;

    public TavoliConDettaglioDeleteDTO(Long id, String numeroNomeTavolo, StatoTavolo statoTavolo, Boolean deleted, LocalDateTime deletedAt) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public TavoliConDettaglioDeleteDTO() {
        this.statoTavolo = StatoTavolo.LIBERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
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
