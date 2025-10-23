package it.paoloadesso.gestionalesala.entities;

import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;


@Entity
@Table(name = "tavoli")
@SQLDelete(sql = "UPDATE tavoli SET deleted = true, deleted_at = NOW() WHERE id_tavolo = ?")
@SQLRestriction("deleted = false")
public class TavoliEntity {

    private static final Logger log = LoggerFactory.getLogger(TavoliEntity.class);
    @Id
    @SequenceGenerator(name = "tavoli_id_gen", sequenceName = "tavoli_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tavoli_id_gen")
    @Column(name = "id_tavolo")
    private Long id;

    @Column(name = "numero_nome_tavolo", nullable = false)
    private String numeroNomeTavolo;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato", nullable = false)
    private StatoTavolo statoTavolo;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public TavoliEntity(Long id, String numeroNomeTavolo, StatoTavolo statoTavolo, Boolean deleted, LocalDateTime deletedAt) {
        this.id = id;
        this.numeroNomeTavolo = numeroNomeTavolo;
        this.statoTavolo = statoTavolo;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public TavoliEntity() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroNomeTavolo() {
        return numeroNomeTavolo;
    }

    public void setNumeroNomeTavolo(String numeroNomeTavolo) {
        this.numeroNomeTavolo = numeroNomeTavolo;
    }

    public StatoTavolo getStatoTavolo() {
        return statoTavolo;
    }

    public void setStatoTavolo(StatoTavolo statoTavolo) {
        this.statoTavolo = statoTavolo;
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

    @PrePersist
    public void prePersist(){
        this.statoTavolo = StatoTavolo.LIBERO;
    }

    // Callback per aggiornare l'oggetto in memoria
    // Esegue il metodo prima che Hibernate chiami il delete, altrimenti deleted
    // e deletedAt rimangono ai valori vecchi.
    @PreRemove
    protected void onSoftDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();

        log.info("Soft delete eseguito del tavolo '{}'", numeroNomeTavolo);
        log.debug("Stato del tavolo al momento della cancellazione: {}", statoTavolo);
    }
}
