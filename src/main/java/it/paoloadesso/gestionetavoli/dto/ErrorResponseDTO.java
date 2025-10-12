package it.paoloadesso.gestionetavoli.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private final String messaggio;
    private final String codiceErroreNumerico;
    private final String codiceErrore;
    private final LocalDateTime timestamp;

    public ErrorResponseDTO(String messaggio, String codiceErroreNumerico, String codiceErrore) {
        this.messaggio = messaggio;
        this.codiceErroreNumerico = codiceErroreNumerico;
        this.codiceErrore = codiceErrore;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponseDTO(String messaggio, String codiceErrore) {
        this.messaggio = messaggio;
        this.codiceErroreNumerico = null;  // Nullable
        this.codiceErrore = codiceErrore;
        this.timestamp = LocalDateTime.now();
    }

    public String getMessaggio() { return messaggio; }
    public String getCodiceErroreNumerico() {
        return codiceErroreNumerico;
    }
    public String getCodiceErrore() { return codiceErrore; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
