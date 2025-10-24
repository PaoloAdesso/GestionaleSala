package it.paoloadesso.gestionalesala.dto;

import java.util.List;

public class RisultatoModificaTavoloDTO {

    private TavoliDTO tavolo;
    private List<String> campiModificati;
    private boolean operazioneCompleta;
    private String messaggio;

    public RisultatoModificaTavoloDTO() {}

    public RisultatoModificaTavoloDTO(TavoliDTO tavolo,
                                      List<String> campiModificati,
                                      boolean operazioneCompleta,
                                      String messaggio) {
        this.tavolo = tavolo;
        this.campiModificati = campiModificati;
        this.operazioneCompleta = operazioneCompleta;
        this.messaggio = messaggio;
    }

    public TavoliDTO getTavolo() { return tavolo; }
    public void setTavolo(TavoliDTO tavolo) { this.tavolo = tavolo; }

    public List<String> getCampiModificati() { return campiModificati; }
    public void setCampiModificati(List<String> campiModificati) { this.campiModificati = campiModificati; }

    public boolean isOperazioneCompleta() { return operazioneCompleta; }
    public void setOperazioneCompleta(boolean operazioneCompleta) { this.operazioneCompleta = operazioneCompleta; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    @Override
    public String toString() {
        return "RisultatoModificaTavoloDTO{" +
                "campiModificati=" + campiModificati +
                ", operazioneCompleta=" + operazioneCompleta +
                ", messaggio='" + messaggio + '\'' +
                '}';
    }
}
