package it.paoloadesso.gestionetavoli.repositories;

import it.paoloadesso.gestionetavoli.entities.ProdottiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiEntity, Long> {

    boolean existsByNomeIgnoreCase(String nomeProdotto);

    List<ProdottiEntity> findByNomeContainingIgnoreCase (String nomeProdotto);

    List<ProdottiEntity> findByCategoriaContainingIgnoreCase (String nomeCategoria);

    @Query("SELECT DISTINCT p.categoria FROM ProdottiEntity p ORDER BY p.categoria")
    List<String> findAllCategorieDistinct();

    /**
     * Trova un prodotto per ID IGNORANDO il filtro @SQLRestriction.
     * Usa SQL nativo per bypassare i filtri Hibernate.
     */
    @Query(value = "SELECT * FROM prodotti WHERE id_prodotto = :id", nativeQuery = true)
    Optional<ProdottiEntity> findByIdInclusoEliminati(@Param("id") Long id);

    /**
     * Trova tutti i prodotti eliminati.
     * Usa SQL nativo per bypassare i filtri Hibernate.
     */
    @Query(value = "SELECT * FROM prodotti WHERE deleted = true", nativeQuery = true)
    List<ProdottiEntity> findAllProdottiEliminati();

    @Query(value = "SELECT * FROM prodotti WHERE nome_prodotto ILIKE CONCAT('%', :nomeProdotto, '%')", nativeQuery = true)
    List<ProdottiEntity> findByNomeContainingIgnoreCaseNative (@Param("nomeProdotto") String nomeProdotto);
}
