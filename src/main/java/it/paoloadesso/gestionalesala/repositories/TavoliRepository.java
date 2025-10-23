package it.paoloadesso.gestionalesala.repositories;

import it.paoloadesso.gestionalesala.entities.OrdiniEntity;
import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TavoliRepository extends JpaRepository<TavoliEntity, Long> {

    boolean existsByNumeroNomeTavoloIgnoreCase(String numeroNomeTavolo);

    List<TavoliEntity> findByStatoTavolo(StatoTavolo statoTavolo);

    @Query(value = "SELECT * FROM tavoli WHERE id_tavolo = :id", nativeQuery = true)
    Optional<TavoliEntity> findByIdInclusoEliminati(@Param("id") Long id);

    @Query(value = "SELECT * FROM tavoli WHERE deleted = true", nativeQuery = true)
    List<TavoliEntity> findAllTavoliEliminati();

    @Modifying
    @Query(value = "DELETE FROM tavoli WHERE id_tavolo = :id", nativeQuery = true)
    void deletePhysically(@Param("id") Long id);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM tavoli WHERE id_tavolo = :id AND deleted = true)",
            nativeQuery = true)
    boolean existsDeletedTavolo(@Param("id") Long id);
}
