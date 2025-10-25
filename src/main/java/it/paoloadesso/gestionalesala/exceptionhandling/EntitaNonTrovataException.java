package it.paoloadesso.gestionalesala.exceptionhandling;

public class EntitaNonTrovataException extends RuntimeException {

    public EntitaNonTrovataException(String messaggio) {
        super(messaggio);
    }

    public EntitaNonTrovataException(String tipoEntita, Long id) {
        super(tipoEntita + " con ID " + id + " non trovato");
    }
}
