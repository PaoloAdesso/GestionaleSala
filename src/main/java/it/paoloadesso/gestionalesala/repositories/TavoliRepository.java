package it.paoloadesso.gestionalesala.repositories;

import it.paoloadesso.gestionalesala.entities.TavoliEntity;
import it.paoloadesso.gestionalesala.enums.StatoTavolo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TavoliRepository extends JpaRepository<TavoliEntity, Long> {

    boolean existsByNumeroNomeTavoloIgnoreCase(String numeroNomeTavolo);

    List<TavoliEntity> findByStatoTavolo(StatoTavolo statoTavolo);
}
