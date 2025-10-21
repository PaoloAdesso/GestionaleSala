package it.paoloadesso.gestionalesala.repositories;

import it.paoloadesso.gestionalesala.dto.OrdiniDTO;
import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrdiniRepository extends JpaRepository<OrdiniEntity, Long> {

    List<OrdiniEntity> findByTavoloId(Long idTavolo);

    List<OrdiniEntity> findByDataOrdine(LocalDate data);

    List<OrdiniEntity> findByTavoloIdAndDataOrdine(Long idTavolo, LocalDate data);

    List<OrdiniEntity> findByTavoloIdAndStatoOrdineNot(Long idTavolo, StatoOrdine stato);

    List<OrdiniEntity> findByStatoOrdineNot(StatoOrdine stato);

    List<OrdiniEntity> findByStatoOrdine(StatoOrdine stato);

    List<OrdiniEntity> findByStatoOrdineAndDataOrdine(StatoOrdine stato, LocalDate data);

    List<OrdiniEntity> findByDataOrdineAndStatoOrdineNot(LocalDate data, StatoOrdine stato);

    List<OrdiniEntity> findByTavoloIdAndDataOrdineAndStatoOrdineNot(Long idTavolo, LocalDate data, StatoOrdine stato);

    OrdiniEntity findByIdOrdine(Long idOrdine);

    @Query("SELECT new it.paoloadesso.gestionalesala.dto.OrdiniDTO(" +
            "o.idOrdine, o.tavolo.id, o.dataOrdine, o.statoOrdine) " +
            "FROM OrdiniEntity o WHERE o.statoOrdine <> :stato")
    List<OrdiniEntity> findOrdiniDtoByStatoOrdineNot(@Param("stato") StatoOrdine statoOrdine);

    @Query(value = "SELECT * FROM ordini WHERE deleted = true ORDER BY deleted_at DESC", nativeQuery = true)
    List<OrdiniEntity> findDeletedOrdini();

    @Query(value = "SELECT * FROM ordini WHERE id_ordine = :id AND deleted = true", nativeQuery = true)
    Optional<OrdiniEntity> findDeletedOrdineById(@Param("id") Long id);

    @Query(value = "SELECT * FROM ordini WHERE deleted = true", nativeQuery = true)
    List<OrdiniEntity> findAllOrdiniEliminati();

    @Query(value = "SELECT EXISTS(SELECT 1 FROM ordini WHERE id_ordine = :id AND deleted = true)",
            nativeQuery = true)
    boolean existsDeletedOrdine(@Param("id") Long id);
}
