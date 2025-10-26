package it.paoloadesso.gestionalesala.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreaTavoliRequestDTO {

    @NotBlank(message = "Il numero/nome tavolo è obbligatorio")
    @Size(min = 1, max = 50, message = "Il numero/nome tavolo deve essere tra 1 e 50 caratteri")
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

    @Override
    public String toString() {
        return "CreaTavoliRequestDTO{" +
                "numeroNomeTavolo='" + numeroNomeTavolo + '\'' +
                '}';
    }
}
