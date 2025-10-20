package it.paoloadesso.gestionalesala.dto;

import it.paoloadesso.gestionalesala.enums.StatoTavolo;

public class TavoloApertoDTO {
    private Long tavoloId;
    private String numeroNomeTavolo;
    private StatoTavolo statoTavolo;

    public TavoloApertoDTO(Long tavoloId, String numeroNomeTavolo, StatoTavolo statoTavolo) {
        this.tavoloId = tavoloId;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
    }

    public TavoloApertoDTO() {
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
    }

    public Long getTavoloId() {
        return tavoloId;
    }

    public void setTavoloId(Long tavoloId) {
        this.tavoloId = tavoloId;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }
}
