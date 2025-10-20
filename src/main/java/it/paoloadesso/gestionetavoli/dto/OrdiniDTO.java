package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class OrdiniDTO {

    @NotNull
    private Long idOrdine;

    @NotNull
    private Long idTavolo;

    @NotNull
    private LocalDate dataOrdine;

    @NotNull
    private StatoOrdine statoOrdine;

    public OrdiniDTO() {
    }

    public OrdiniDTO(Long idOrdine, Long idTavolo, LocalDate dataOrdine, StatoOrdine statoOrdine) {
        this.idOrdine = idOrdine;
        this.idTavolo = idTavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
    }
}
