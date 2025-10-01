package it.paoloadesso.gestionetavoli.entities;

import jakarta.persistence.*;

import java.math.BigDecimal;


@Entity
@Table(name = "prodotti")
public class ProdottiEntity {

    @Id
    @SequenceGenerator(name = "prodotti_id_gen", sequenceName = "prodotti_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "prodotti_id_gen")
    @Column(name = "id_prodotto")
    private Long id;

    @Column(name = "nome_prodotto", nullable = false)
    private String nome;

    @Column(name = "categoria_prodotto", nullable = false)
    private String categoria;

    @Column(name = "prezzo", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    public ProdottiEntity(Long id, String nome, String categoria, BigDecimal prezzo) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
    }

    public ProdottiEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrezzo() {
        return prezzo;
    }

    public void setPrezzo(BigDecimal prezzo) {
        this.prezzo = prezzo;
    }
}
