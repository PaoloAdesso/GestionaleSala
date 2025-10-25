package it.paoloadesso.gestionalesala.exceptionhandling;

import it.paoloadesso.gestionalesala.enums.StatoOrdine;

public class TransizioneStatoNonValidaException extends RuntimeException {
    public TransizioneStatoNonValidaException(StatoOrdine statoAttuale, StatoOrdine nuovoStato) {
        super("Transizione non valida da " + statoAttuale + " a " + nuovoStato);
    }
}
