package it.paoloadesso.gestionetavoli.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class ModificaOrdineRequestDTO {

    @Schema(description = "Nuovo ID del tavolo (opzionale, invia solo se vuoi cambiare tavolo)",
            example = "5",
            nullable = true)
    @Positive
    private Long nuovoIdTavolo;

    @Valid
    @Schema(description = "Lista prodotti da aggiungere all'ordine (opzionale)",
            nullable = true)
    private List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere;

    @Valid
    @Schema(description = "Lista prodotti da rimuovere dall'ordine (opzionale)",
            nullable = true)
    private List<ProdottiDaRimuovereDTO> prodottiDaRimuovere;

    public ModificaOrdineRequestDTO(Long nuovoIdTavolo, List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere, List<ProdottiDaRimuovereDTO> prodottiDaRimuovere) {
        this.nuovoIdTavolo = nuovoIdTavolo;
        this.prodottiDaAggiungere = prodottiDaAggiungere;
        this.prodottiDaRimuovere = prodottiDaRimuovere;
    }

    public ModificaOrdineRequestDTO() {
    }

    public Long getNuovoIdTavolo() {
        return nuovoIdTavolo;
    }

    public void setNuovoIdTavolo(Long nuovoIdTavolo) {
        this.nuovoIdTavolo = nuovoIdTavolo;
    }

    public List<ProdottiOrdinatiRequestDTO> getProdottiDaAggiungere() {
        return prodottiDaAggiungere;
    }

    public void setProdottiDaAggiungere(List<ProdottiOrdinatiRequestDTO> prodottiDaAggiungere) {
        this.prodottiDaAggiungere = prodottiDaAggiungere;
    }


    public List<ProdottiDaRimuovereDTO> getProdottiDaRimuovere() {
        return prodottiDaRimuovere;
    }

    public void setProdottiDaRimuovere(List<ProdottiDaRimuovereDTO> prodottiDaRimuovere) {
        this.prodottiDaRimuovere = prodottiDaRimuovere;
    }

    @Override
    public String toString() {
        return "ModificaOrdineRequestDto{" +
                "nuovoIdTavolo=" + nuovoIdTavolo +
                ", prodottiDaAggiungere=" + prodottiDaAggiungere +
                ", prodottiDaRimuovere=" + prodottiDaRimuovere +
                '}';
    }

    @JsonIgnore
    public boolean isEmpty() {
        return nuovoIdTavolo == null &&
                (prodottiDaAggiungere == null || prodottiDaAggiungere.isEmpty()) &&
                (prodottiDaRimuovere == null || prodottiDaRimuovere.isEmpty());
    }
}
