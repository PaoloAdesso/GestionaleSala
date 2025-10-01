package it.paoloadesso.gestionetavoli.entities.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class OrdiniProdottiId implements Serializable {
    @Column(name = "id_ordine")
    private Long idOrdine;

    @Column(name = "id_prodotto")
    private Long idProdotto;

    public OrdiniProdottiId(Long idOrdine, Long idProdotto) {
        this.idOrdine = idOrdine;
        this.idProdotto = idProdotto;
    }

    public OrdiniProdottiId() {
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Long getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Long idProdotto) {
        this.idProdotto = idProdotto;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OrdiniProdottiId that = (OrdiniProdottiId) o;
        return Objects.equals(idOrdine, that.idOrdine) && Objects.equals(idProdotto, that.idProdotto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idOrdine, idProdotto);
    }
}
