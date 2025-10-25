package it.paoloadesso.gestionalesala.schedulers;

import it.paoloadesso.gestionalesala.services.OrdiniService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrdiniScheduler {

    private static final Logger log = LoggerFactory.getLogger(OrdiniScheduler.class);

    private final OrdiniService ordiniService;

    public OrdiniScheduler(OrdiniService ordiniService) {
        this.ordiniService = ordiniService;
    }

    @Scheduled(cron = "${ristorante.cron-eliminazione-ordini-settimana-precedente}", zone = "Europe/Rome")
    public void eliminaOrdiniSettimanaPrecedenteProgrammato() {
        log.info("Eliminazione automatica/programmata degli ordini della settimana precedente (hard-delete)");
        ordiniService.eliminaOrdiniSettimanaPrecedente();
    }

    @Scheduled(cron = "${ristorante.cron-eliminazione-soft-ordini-giorno-precedente}", zone = "Europe/Rome")
    public void eliminaOrdiniGiornoPrecedenteProgrammato() {
        log.info("Eliminazione automatica/programmata degli ordini del giorno precedente (soft-delete)");
        ordiniService.eliminaOrdiniGiornoPrecedente();
    }
}

