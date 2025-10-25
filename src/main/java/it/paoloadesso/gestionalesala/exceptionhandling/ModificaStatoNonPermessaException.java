package it.paoloadesso.gestionalesala.exceptionhandling;

import it.paoloadesso.gestionalesala.enums.StatoOrdine;

public class ModificaStatoNonPermessaException extends RuntimeException {
    public ModificaStatoNonPermessaException(String motivo) {
        super(motivo);
    }

    public ModificaStatoNonPermessaException(Long ordineId, StatoOrdine statoAttuale) {
        super("Non è possibile modificare lo stato dell'ordine " + ordineId +
                " che si trova nello stato " + statoAttuale);
    }
}

