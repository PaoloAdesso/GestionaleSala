package it.paoloadesso.gestionalesala.exceptionhandling;

public class CampoVuotoException extends RuntimeException {
    public CampoVuotoException(String nomeCampo) {
        super("Il " + nomeCampo + " non può essere vuoto");
    }
}
