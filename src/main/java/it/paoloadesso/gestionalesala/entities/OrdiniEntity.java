package it.paoloadesso.gestionalesala.entities;

import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "ordini")
@SQLDelete(sql = "UPDATE ordini SET deleted = true, deleted_at = NOW() WHERE id_ordine = ?")
@SQLRestriction("deleted = false")
public class OrdiniEntity {

    private static final Logger log = LoggerFactory.getLogger(OrdiniEntity.class);

    @Id
    @SequenceGenerator(name = "ordini_id_gen", sequenceName = "ordini_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ordini_id_gen")
    @Column(name = "id_ordine")
    private Long idOrdine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_tavolo", nullable = false)
    private TavoliEntity tavolo;

    @Column(name = "data_ordine", nullable = false)
    private LocalDate dataOrdine;

    @Enumerated(EnumType.STRING)
    @Column(name = "stato_ordine", nullable = false)
    private StatoOrdine statoOrdine;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public OrdiniEntity() {
    }

    public OrdiniEntity(Long idOrdine, TavoliEntity tavolo, LocalDate dataOrdine, StatoOrdine statoOrdine, Boolean deleted, LocalDateTime deletedAt) {
        this.idOrdine = idOrdine;
        this.tavolo = tavolo;
        this.dataOrdine = dataOrdine;
        this.statoOrdine = statoOrdine;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public Long getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(Long idOrdine) {
        this.idOrdine = idOrdine;
    }

    public TavoliEntity getTavolo() {
        return tavolo;
    }

    public void setTavolo(TavoliEntity tavolo) {
        this.tavolo = tavolo;
    }

    public LocalDate getDataOrdine() {
        return dataOrdine;
    }

    public void setDataOrdine(LocalDate dataOrdine) {
        this.dataOrdine = dataOrdine;
    }

    public StatoOrdine getStatoOrdine() {
        return statoOrdine;
    }

    public void setStatoOrdine(StatoOrdine statoOrdine) {
        this.statoOrdine = statoOrdine;
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
        this.statoOrdine = StatoOrdine.IN_ATTESA;
    }

    // Callback per aggiornare l'oggetto in memoria
    // Esegue il metodo prima che Hibernate chiami il delete, altrimenti deleted
    // e deletedAt rimangono ai valori vecchi.
    @PreRemove
    protected void onSoftDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();

        String numeroNomeTavolo = (tavolo != null) ? tavolo.getNumeroNomeTavolo() : "N/A";
        log.info("Soft delete eseguito per ordine ID {} del tavolo '{}'", idOrdine, numeroNomeTavolo);
        log.debug("Stato ordine al momento della cancellazione: {}", statoOrdine);
    }
}
