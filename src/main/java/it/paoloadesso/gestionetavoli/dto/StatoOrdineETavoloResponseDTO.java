package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import it.paoloadesso.gestionetavoli.enums.StatoTavolo;

public class StatoOrdineETavoloResponseDTO {
    private Long ordineId;
    private StatoOrdine statoOrdine;
    private boolean isAltriOrdiniAperti;
    private Long tavoloId;
    private StatoTavolo statoTavolo;

    public StatoOrdineETavoloResponseDTO() {
    }

    public StatoOrdineETavoloResponseDTO(Long ordineId, StatoOrdine statoOrdine, boolean isAltriOrdiniAperti, Long tavoloId, StatoTavolo statoTavolo) {
        this.ordineId = ordineId;
        this.statoOrdine = statoOrdine;
        this.isAltriOrdiniAperti = isAltriOrdiniAperti;
        this.tavoloId = tavoloId;
        this.statoTavolo = statoTavolo;
    }

    public Long getOrdineId() {
        return ordineId;
    }

    public void setOrdineId(Long ordineId) {
        this.ordineId = ordineId;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }

    public boolean isAltriOrdiniAperti() {
        return isAltriOrdiniAperti;
    }

    public void setAltriOrdiniAperti(boolean altriOrdiniAperti) {
        this.isAltriOrdiniAperti = altriOrdiniAperti;
    }

    public Long getTavoloId() {
        return tavoloId;
    }

    public void setTavoloId(Long tavoloId) {
        this.tavoloId = tavoloId;
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
    }
}
