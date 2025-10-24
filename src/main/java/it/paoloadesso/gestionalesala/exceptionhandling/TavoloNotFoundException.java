package it.paoloadesso.gestionalesala.exceptionhandling;

public class TavoloNotFoundException extends RuntimeException {
    public TavoloNotFoundException(String message) {
        super(message);
    }

    public TavoloNotFoundException(Long tavoloId) {
        super("Tavolo con ID " + tavoloId + " non trovato");
    }
}
