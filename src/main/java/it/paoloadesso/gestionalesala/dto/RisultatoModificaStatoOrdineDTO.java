package it.paoloadesso.gestionalesala.dto;

import it.paoloadesso.gestionalesala.enums.StatoOrdine;

public class RisultatoModificaStatoOrdineDTO {

    private Long idOrdine;
    private StatoOrdine vecchioStato;
    private StatoOrdine nuovoStato;
    private boolean operazioneCompleta;
    private String messaggio;

    public RisultatoModificaStatoOrdineDTO() {}

    public RisultatoModificaStatoOrdineDTO(Long idOrdine, StatoOrdine vecchioStato,
                                           StatoOrdine nuovoStato, boolean operazioneCompleta,
                                           String messaggio) {
        this.idOrdine = idOrdine;
        this.vecchioStato = vecchioStato;
        this.nuovoStato = nuovoStato;
        this.operazioneCompleta = operazioneCompleta;
        this.messaggio = messaggio;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public StatoOrdine getVecchioStato() {
        return vecchioStato;
    }

    public void setVecchioStato(StatoOrdine vecchioStato) {
        this.vecchioStato = vecchioStato;
    }

    public StatoOrdine getNuovoStato() {
        return nuovoStato;
    }

    public void setNuovoStato(StatoOrdine nuovoStato) {
        this.nuovoStato = nuovoStato;
    }

    public boolean isOperazioneCompleta() {
        return operazioneCompleta;
    }

    public void setOperazioneCompleta(boolean operazioneCompleta) {
        this.operazioneCompleta = operazioneCompleta;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    @Override
    public String toString() {
        return "RisultatoModificaStatoOrdineDTO{" +
                "idOrdine=" + idOrdine +
                ", vecchioStato=" + vecchioStato +
                ", nuovoStato=" + nuovoStato +
                ", operazioneCompleta=" + operazioneCompleta +
                ", messaggio='" + messaggio + '\'' +
                '}';
    }
}
