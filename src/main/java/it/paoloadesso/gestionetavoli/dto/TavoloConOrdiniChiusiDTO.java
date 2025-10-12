package it.paoloadesso.gestionetavoli.dto;

import it.paoloadesso.gestionetavoli.enums.StatoTavolo;

import java.util.List;

public class TavoloConOrdiniChiusiDTO {
    private Long idTavolo;
    private String numeroNomeTavolo;
    private StatoTavolo statoTavolo;
    private List<OrdineMinimalDTO> ordini;

    public TavoloConOrdiniChiusiDTO() {}

    public TavoloConOrdiniChiusiDTO(Long idTavolo, String numeroNomeTavolo,
                                    StatoTavolo statoTavolo, List<OrdineMinimalDTO> ordini) {
        this.idTavolo = idTavolo;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
        this.ordini = ordini;
    }

    public Long getIdTavolo() {
        return idTavolo;
    }

    public void setIdTavolo(Long idTavolo) {
        this.idTavolo = idTavolo;
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

    public List<OrdineMinimalDTO> getOrdini() {
        return ordini;
    }

    public void setOrdini(List<OrdineMinimalDTO> ordini) {
        this.ordini = ordini;
    }
}
