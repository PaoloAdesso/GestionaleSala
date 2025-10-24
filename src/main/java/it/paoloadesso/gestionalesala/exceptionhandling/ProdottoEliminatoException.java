package it.paoloadesso.gestionalesala.exceptionhandling;

public class ProdottoEliminatoException extends RuntimeException {
    public ProdottoEliminatoException(String message) {
        super(message);
    }

    public ProdottoEliminatoException(Long prodottoId) {
        super("Impossibile modificare il prodotto con ID " + prodottoId + ": è stato eliminato");
    }
}