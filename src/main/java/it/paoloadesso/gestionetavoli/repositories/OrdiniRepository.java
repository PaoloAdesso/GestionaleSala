package it.paoloadesso.gestionetavoli.repositories;

import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

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
}
