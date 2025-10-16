package it.paoloadesso.gestionetavoli.dto;

public class TavoloEliminatoDTO {
    private Long tavoloId;
    private String numeroNomeTavolo;

    public TavoloEliminatoDTO(Long tavoloId, String numeroNomeTavolo) {
        this.tavoloId = tavoloId;
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public TavoloEliminatoDTO() {
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
