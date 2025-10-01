package it.paoloadesso.gestionetavoli.repositories;

import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdiniProdottiRepository extends JpaRepository<OrdiniProdottiEntity, OrdiniProdottiId> {

    List<OrdiniProdottiEntity> findByOrdine_Tavolo_Id(Long idTavolo);

    List<OrdiniProdottiEntity> findByOrdine_Tavolo_IdAndOrdine_StatoOrdineNot(Long idTavolo, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(Long idTavolo, LocalDate data, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdine_IdOrdine(Long idOrdine);
}
