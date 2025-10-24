package it.paoloadesso.gestionalesala.exceptionhandling;

public class TavoloEliminatoException extends RuntimeException {
    public TavoloEliminatoException(String message) {
        super(message);
    }

    public TavoloEliminatoException(Long tavoloId) {
        super("Impossibile modificare il tavolo con ID " + tavoloId + ": è stato eliminato");
    }
}
