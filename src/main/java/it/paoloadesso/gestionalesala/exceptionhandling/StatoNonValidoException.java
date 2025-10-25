package it.paoloadesso.gestionalesala.exceptionhandling;

public class StatoNonValidoException extends RuntimeException {
    public StatoNonValidoException(String operazione, String statoAttuale) {
        super("Impossibile " + operazione + ": " + statoAttuale);
    }
}
