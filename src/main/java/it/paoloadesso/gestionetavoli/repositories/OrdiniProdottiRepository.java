package it.paoloadesso.gestionetavoli.repositories;

import it.paoloadesso.gestionetavoli.entities.OrdiniEntity;
import it.paoloadesso.gestionetavoli.entities.OrdiniProdottiEntity;
import it.paoloadesso.gestionetavoli.entities.keys.OrdiniProdottiId;
import it.paoloadesso.gestionetavoli.enums.StatoOrdine;
import it.paoloadesso.gestionetavoli.enums.StatoPagato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrdiniProdottiRepository extends JpaRepository<OrdiniProdottiEntity, OrdiniProdottiId> {

    List<OrdiniProdottiEntity> findByOrdineTavoloId(Long idTavolo);

    List<OrdiniProdottiEntity> findByOrdineTavoloIdAndOrdineStatoOrdineNot(Long idTavolo, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdineTavoloIdAndOrdineDataOrdineAndOrdineStatoOrdineNot(Long idTavolo, LocalDate data, StatoOrdine stato);

    List<OrdiniProdottiEntity> findByOrdineIdOrdine(Long idOrdine);

    List<OrdiniProdottiEntity> findByOrdineAndStatoPagato(OrdiniEntity ordine, StatoPagato statoPagato);

    // Trova prodotti di un ordine con stato pagamento specifico
    List<OrdiniProdottiEntity> findByOrdineIdOrdineAndStatoPagato(Long idOrdine, StatoPagato statoPagato);

    // Conta prodotti pagati in un ordine
    @Query("SELECT COUNT(op) FROM OrdiniProdottiEntity op WHERE op.ordine.idOrdine = :idOrdine AND op.statoPagato = :statoPagato")
    Integer countByOrdineIdOrdineAndStatoPagato(@Param("idOrdine") Long idOrdine, @Param("statoPagato") StatoPagato statoPagato);

    // Conta totale prodotti in un ordine
    @Query("SELECT COUNT(op) FROM OrdiniProdottiEntity op WHERE op.ordine.idOrdine = :idOrdine")
    Integer countByOrdineIdOrdine(@Param("idOrdine") Long idOrdine);

    boolean existsByProdottoIdAndOrdineStatoOrdineNot(Long idProdotto, StatoOrdine statoOrdine);
}
