package it.paoloadesso.gestionalesala.dto;

import jakarta.validation.constraints.NotBlank;

public class CreaTavoliRequestDTO {

    @NotBlank
    private String numeroNomeTavolo;

    public CreaTavoliRequestDTO(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public CreaTavoliRequestDTO() {

    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

}
