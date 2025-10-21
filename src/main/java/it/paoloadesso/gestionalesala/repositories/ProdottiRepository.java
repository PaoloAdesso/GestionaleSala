package it.paoloadesso.gestionalesala.repositories;

import it.paoloadesso.gestionalesala.entities.ProdottiEntity;
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

    @Query(value = "SELECT * FROM prodotti WHERE id_prodotto = :id", nativeQuery = true)
    Optional<ProdottiEntity> findByIdInclusoEliminati(@Param("id") Long id);

    @Query(value = "SELECT * FROM prodotti WHERE deleted = true", nativeQuery = true)
    List<ProdottiEntity> findAllProdottiEliminati();

    @Query(value = "SELECT * FROM prodotti WHERE nome_prodotto ILIKE CONCAT('%', :nomeProdotto, '%')", nativeQuery = true)
    List<ProdottiEntity> findByNomeContainingIgnoreCaseNative (@Param("nomeProdotto") String nomeProdotto);

    @Query(value = "SELECT EXISTS(SELECT 1 FROM prodotti WHERE id_prodotto = :id AND deleted = true)",
            nativeQuery = true)
    boolean existsDeletedProdotto(@Param("id") Long id);
}
