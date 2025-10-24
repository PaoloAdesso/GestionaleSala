package it.paoloadesso.gestionalesala.dto;

import java.util.List;

public class RisultatoModificaProdottoDTO {

    private ProdottiDTO prodotto;
    private List<String> campiModificati;
    private boolean operazioneCompleta;
    private String messaggio;

    public RisultatoModificaProdottoDTO() {
    }

    public RisultatoModificaProdottoDTO(ProdottiDTO prodotto, List<String> campiModificati, boolean operazioneCompleta, String messaggio) {
        this.prodotto = prodotto;
        this.campiModificati = campiModificati;
        this.operazioneCompleta = operazioneCompleta;
        this.messaggio = messaggio;
    }

    public ProdottiDTO getProdotto() {
        return prodotto;
    }

    public void setProdotto(ProdottiDTO prodotto) {
        this.prodotto = prodotto;
    }

    public List<String> getCampiModificati() {
        return campiModificati;
    }

    public void setCampiModificati(List<String> campiModificati) {
        this.campiModificati = campiModificati;
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
        return "RisultatoModificaProdottoDTO{" +
                ", campiModificati=" + campiModificati +
                ", operazioneCompleta=" + operazioneCompleta +
                ", messaggio='" + messaggio + '\'' +
                '}';
    }
}