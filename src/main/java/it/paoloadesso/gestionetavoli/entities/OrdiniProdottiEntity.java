package it.paoloadesso.gestionetavoli.entities;

import it.paoloadesso.gestionetavoli.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import jakarta.persistence.*;

@Entity
@Table(name = "ordini_prodotti")
public class OrdiniProdottiEntity {

    @EmbeddedId
    private OrdiniProdottiId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idOrdine")
    @JoinColumn(name = "id_ordine", nullable = false)
    private OrdiniEntity ordine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("idProdotto")
    @JoinColumn(name = "id_prodotto", nullable = false)
    private ProdottiEntity prodotto;

    @Column(name = "quantita_prodotto", nullable = false)
    private Integer quantitaProdotto;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_pagato", nullable = false)
    private StatoPagato statoPagato;

    public OrdiniProdottiEntity() {
    }

    public OrdiniProdottiEntity(OrdiniProdottiId id, OrdiniEntity ordine, ProdottiEntity prodotto, Integer quantitaProdotto, StatoPagato statoPagato) {
        this.id = id;
        this.ordine = ordine;
        this.prodotto = prodotto;
        this.quantitaProdotto = quantitaProdotto;
        this.statoPagato = statoPagato;
    }

    public OrdiniProdottiId getId() {
        return id;
    }

    public void setId(OrdiniProdottiId id) {
        this.id = id;
    }

    public OrdiniEntity getOrdine() {
        return ordine;
    }

    public void setOrdine(OrdiniEntity ordine) {
        this.ordine = ordine;
    }

    public ProdottiEntity getProdotto() {
        return prodotto;
    }

    public void setProdotto(ProdottiEntity prodotto) {
        this.prodotto = prodotto;
    }

    public Integer getQuantitaProdotto() {
        return quantitaProdotto;
    }

    public void setQuantitaProdotto(Integer quantitaProdotto) {
        this.quantitaProdotto = quantitaProdotto;
    }

    public StatoPagato getStatoPagato() {
        return statoPagato;
    }

    public void setStatoPagato(StatoPagato statoPagato) {
        this.statoPagato = statoPagato;
    }

    @PrePersist
    public void prePersist(){
        this.statoPagato = StatoPagato.NON_PAGATO;
    }
}
