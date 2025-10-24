package it.paoloadesso.gestionalesala.exceptionhandling;

public class ModificaVuotaException extends RuntimeException {
    public ModificaVuotaException() {
        super("La richiesta di modifica è vuota");
    }

    public ModificaVuotaException(String message) {
        super(message);
    }
}
