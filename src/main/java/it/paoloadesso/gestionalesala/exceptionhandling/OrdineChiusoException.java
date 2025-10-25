package it.paoloadesso.gestionalesala.exceptionhandling;

public class OrdineChiusoException extends RuntimeException {
    public OrdineChiusoException(Long ordineId) {
        super("L'ordine con ID " + ordineId + " è già chiuso e non può essere modificato");
    }
}
