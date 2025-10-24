package it.paoloadesso.gestionalesala.exceptionhandling;

public class ProdottoNotFoundException extends RuntimeException {
    public ProdottoNotFoundException(String message) {
        super(message);
    }

    public ProdottoNotFoundException(Long prodottoId) {
        super("Prodotto con ID " + prodottoId + " non trovato");
    }
}