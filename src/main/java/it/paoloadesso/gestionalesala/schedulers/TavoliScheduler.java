package it.paoloadesso.gestionalesala.schedulers;

import it.paoloadesso.gestionalesala.services.TavoliService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TavoliScheduler {

    private static final Logger log = LoggerFactory.getLogger(TavoliScheduler.class); // ← CORRETTO

    private final TavoliService tavoliService;

    public TavoliScheduler(TavoliService tavoliService) {
        this.tavoliService = tavoliService;
    }

    @Scheduled(cron = "${ristorante.cron-reset-tavoli}", zone = "Europe/Rome")
    public void liberaTuttiITavoliProgrammato() {
        log.info("Reset automatico tavoli - Inizio turno lavorativo");
        tavoliService.liberaTuttiITavoli();
    }
}
