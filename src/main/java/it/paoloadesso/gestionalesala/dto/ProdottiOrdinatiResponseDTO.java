package it.paoloadesso.gestionalesala.dto;

import it.paoloadesso.gestionalesala.enums.StatoPagato;

import java.util.Objects;

public class ProdottiOrdinatiResponseDTO {
        private Long idProdotto;
        private Integer quantitaProdotto;
        private StatoPagato statoPagato;

        public Long getIdProdotto() {
            return idProdotto;
        }

        public void setIdProdotto(Long idProdotto) {
            this.idProdotto = idProdotto;
        }

        public Integer getQuantitaProdotto() {
            return quantitaProdotto;
        }

        public void setQuantitaProdotto(Integer quantitaProdotto) {
            this.quantitaProdotto = quantitaProdotto;
        }

        public StatoPagato getStatoPagato() {
            return statoPagato;
        }

        public void setStatoPagato(StatoPagato statoPagato) {
            this.statoPagato = statoPagato;
        }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ProdottiOrdinatiResponseDTO that = (ProdottiOrdinatiResponseDTO) o;
        return Objects.equals(idProdotto, that.idProdotto) && Objects.equals(quantitaProdotto, that.quantitaProdotto) && statoPagato == that.statoPagato;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProdotto, quantitaProdotto, statoPagato);
    }

    public ProdottiOrdinatiResponseDTO() {
    }

    public ProdottiOrdinatiResponseDTO(Long idProdotto, Integer quantitaProdotto, StatoPagato statoPagato) {
        this.idProdotto = idProdotto;
        this.quantitaProdotto = quantitaProdotto;
        this.statoPagato = statoPagato;
    }
}
