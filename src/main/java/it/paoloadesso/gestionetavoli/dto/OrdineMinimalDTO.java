package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoOrdine;

import java.time.LocalDate;

public class OrdineMinimalDTO {
    private Long idOrdine;
    private LocalDate dataOrdine;
    private StatoOrdine statoOrdine;

    public OrdineMinimalDTO() {}

    public OrdineMinimalDTO(Long idOrdine, LocalDate dataOrdine, StatoOrdine statoOrdine) {
        this.idOrdine = idOrdine;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
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

