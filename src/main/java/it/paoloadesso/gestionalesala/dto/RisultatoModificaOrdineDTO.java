package it.paoloadesso.gestionalesala.dto;

import java.util.List;

/**
 * Questo DTO gestisce i risultati delle modifiche agli ordini.
 * È utile quando alcuni prodotti vengono aggiunti correttamente ma altri danno errore,
 * così posso comunicare all'utente cosa è andato bene e cosa no (successo parziale).
 */
public class RisultatoModificaOrdineDTO {

    private ListaOrdiniEProdottiByTavoloResponseDTO ordine;
    private int prodottiAggiunti;
    private int prodottiRimossi;
    private List<String> errori;
    private boolean operazioneCompleta;
    private String messaggio;

    public RisultatoModificaOrdineDTO() {}

    public RisultatoModificaOrdineDTO(ListaOrdiniEProdottiByTavoloResponseDTO ordine,
                                      int prodottiAggiunti,
                                      int prodottiRimossi,
                                      List<String> errori,
                                      boolean operazioneCompleta,
                                      String messaggio) {
        this.ordine = ordine;
        this.prodottiAggiunti = prodottiAggiunti;
        this.prodottiRimossi = prodottiRimossi;
        this.errori = errori;
        this.operazioneCompleta = operazioneCompleta;
        this.messaggio = messaggio;
    }

    public ListaOrdiniEProdottiByTavoloResponseDTO getOrdine() { return ordine; }
    public void setOrdine(ListaOrdiniEProdottiByTavoloResponseDTO ordine) { this.ordine = ordine; }

    public int getProdottiAggiunti() { return prodottiAggiunti; }
    public void setProdottiAggiunti(int prodottiAggiunti) { this.prodottiAggiunti = prodottiAggiunti; }

    public int getProdottiRimossi() { return prodottiRimossi; }
    public void setProdottiRimossi(int prodottiRimossi) { this.prodottiRimossi = prodottiRimossi; }

    public List<String> getErrori() { return errori; }
    public void setErrori(List<String> errori) { this.errori = errori; }

    public boolean isOperazioneCompleta() { return operazioneCompleta; }
    public void setOperazioneCompleta(boolean operazioneCompleta) { this.operazioneCompleta = operazioneCompleta; }

    public String getMessaggio() { return messaggio; }
    public void setMessaggio(String messaggio) { this.messaggio = messaggio; }

    @Override
    public String toString() {
        return "RisultatoModificaOrdineDto{" +
                "prodottiAggiunti=" + prodottiAggiunti +
                ", prodottiRimossi=" + prodottiRimossi +
                ", errori=" + errori +
                ", operazioneCompleta=" + operazioneCompleta +
                ", messaggio='" + messaggio + '\'' +
                '}';
    }
}
