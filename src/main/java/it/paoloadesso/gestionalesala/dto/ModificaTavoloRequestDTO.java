package it.paoloadesso.gestionalesala.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import jakarta.validation.constraints.Size;

public class ModificaTavoloRequestDTO {

    @Size(min = 1, max = 50, message = "Il nome/numero tavolo deve essere tra 1 e 50 caratteri")
    private String numeroNomeTavolo;

    private StatoTavolo statoTavolo;

    public ModificaTavoloRequestDTO() {}

    public ModificaTavoloRequestDTO(String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public String getNumeroNomeTavolo() { return numeroNomeTavolo; }
    public void setNumeroNomeTavolo(String numeroNomeTavolo) { this.numeroNomeTavolo = numeroNomeTavolo; }

    public StatoTavolo getStatoTavolo() { return statoTavolo; }
    public void setStatoTavolo(StatoTavolo statoTavolo) { this.statoTavolo = statoTavolo; }

    @JsonIgnore
    public boolean isEmpty() {
        return numeroNomeTavolo == null && statoTavolo == null;
    }

    @Override
    public String toString() {
        return "ModificaTavoloRequestDTO{" +
                "numeroNomeTavolo='" + numeroNomeTavolo + '\'' +
                ", statoTavolo=" + statoTavolo +
                '}';
    }
}
