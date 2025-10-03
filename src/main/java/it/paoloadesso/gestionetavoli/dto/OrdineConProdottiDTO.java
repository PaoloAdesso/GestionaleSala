package it.paoloadesso.gestionetavoli.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrdineConProdottiDTO {
    private Long ordineId;
    private BigDecimal totaleOrdine;
    private List<ProdottoNonPagatoDTO> prodotti; // ← Lista di DTO prodotti

    public OrdineConProdottiDTO(Long ordineId, BigDecimal totaleOrdine, List<ProdottoNonPagatoDTO> prodotti) {
        this.ordineId = ordineId;
        this.totaleOrdine = totaleOrdine;
        this.prodotti = prodotti;
    }

    public OrdineConProdottiDTO() {
    }

    public Long getOrdineId() {
        return ordineId;
    }

    public void setOrdineId(Long ordineId) {
        this.ordineId = ordineId;
    }

    public BigDecimal getTotaleOrdine() {
        return totaleOrdine;
    }

    public void setTotaleOrdine(BigDecimal totaleOrdine) {
        this.totaleOrdine = totaleOrdine;
    }

    public List<ProdottoNonPagatoDTO> getProdotti() {
        return prodotti;
    }

    public void setProdotti(List<ProdottoNonPagatoDTO> prodotti) {
        this.prodotti = prodotti;
    }
}
