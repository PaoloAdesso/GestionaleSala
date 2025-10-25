package it.paoloadesso.gestionalesala.exceptionhandling;

public class EntitaGiaEsistenteException extends RuntimeException {
    public EntitaGiaEsistenteException(String tipoEntita, String identificatore) {
        super("Esiste già " + tipoEntita + " con " + identificatore);
    }
}
