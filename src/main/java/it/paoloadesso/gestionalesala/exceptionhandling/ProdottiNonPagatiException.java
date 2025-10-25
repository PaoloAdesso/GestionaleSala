package it.paoloadesso.gestionalesala.exceptionhandling;

public class ProdottiNonPagatiException extends RuntimeException {
    public ProdottiNonPagatiException(Long ordineId, int numProdottiNonPagati) {
        super("Impossibile chiudere l'ordine " + ordineId +
                ": " + numProdottiNonPagati + " prodotti non risultano pagati");
    }
}