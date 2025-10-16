package it.paoloadesso.gestionetavoli.dto;

import jakarta.validation.constraints.NotBlank;

public class CreaTavoliDTO {

    @NotBlank
    private String numeroNomeTavolo;

    public CreaTavoliDTO(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public CreaTavoliDTO() {

    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

}
