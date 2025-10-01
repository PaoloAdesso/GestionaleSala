package it.paoloadesso.gestionetavoli.repositories;

import it.paoloadesso.gestionetavoli.entities.ProdottiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProdottiRepository extends JpaRepository<ProdottiEntity, Long> {

    boolean existsByNomeIgnoreCase(String nomeProdotto);

    List<ProdottiEntity> findByNomeContainingIgnoreCase (String nomeProdotto);

    List<ProdottiEntity> findByCategoriaContainingIgnoreCase (String nomeCategoria);
}
