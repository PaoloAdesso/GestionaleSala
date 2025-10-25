package it.paoloadesso.gestionalesala.exceptionhandling;

public class RichiestaVuotaException extends RuntimeException {
    public RichiestaVuotaException(String tipoRichiesta) {
        super("La richiesta di " + tipoRichiesta + " è vuota");
    }
}
