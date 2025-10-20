package it.paoloadesso.gestionalesala.entities;

import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@Table(name = "prodotti")
@SQLDelete(sql = "UPDATE prodotti SET deleted = true, deleted_at = NOW() WHERE id_prodotto = ?")
@SQLRestriction("deleted = false")
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

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public ProdottiEntity(Long id, String nome, String categoria, BigDecimal prezzo, Boolean deleted, LocalDateTime deletedAt) {
        this.id = id;
        this.nome = nome;
        this.categoria = categoria;
        this.prezzo = prezzo;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // Callback per aggiornare l'oggetto in memoria
    // Esegue il metodo prima che Hibernate chiami il delete, altrimenti deleted
    // e deletedAt rimangono ai valori vecchi.
    @PreRemove
    protected void onSoftDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        System.out.println("Soft delete prodotto «" + nome + "» con ID " + id);
    }
}
