package it.paoloadesso.gestionetavoli.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Utility per calcolare la "data lavorativa" considerando il turno del ristorante.
 * Se il ristorante chiude alle 6:00, gli ordini dopo la mezzanotte
 * appartengono ancora alla giornata lavorativa del giorno precedente.
 */
@Component
public class DataLavorativaUtil {

    @Value("${ristorante.orario-chiusura-turno:06:00}")
    private String orarioChiusuraTurno;

    /**
     * Calcola la "data lavorativa" corrente basata sull'orario di chiusura del turno.
     *
     * Esempio:
     * - Ore 23:00 del 18 ottobre → ritorna 18 ottobre
     * - Ore 01:00 del 19 ottobre → ritorna 18 ottobre (turno non ancora finito)
     * - Ore 07:00 del 19 ottobre → ritorna 19 ottobre (nuovo turno iniziato)
     *
     * @return la data del turno lavorativo corrente
     */
    public LocalDate getDataLavorativa() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime chiusura = LocalTime.parse(orarioChiusuraTurno);

        // Se sono tra 00:00 e l'ora di chiusura turno, sto ancora nel turno del giorno prima
        if (now.toLocalTime().isBefore(chiusura)) {
            return now.toLocalDate().minusDays(1);
        }

        return now.toLocalDate();
    }
}
