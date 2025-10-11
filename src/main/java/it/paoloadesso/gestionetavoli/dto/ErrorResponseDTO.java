package it.paoloadesso.gestionetavoli.dto;

import java.time.LocalDateTime;

public class ErrorResponseDTO {
    private final String messaggio;
    private final String codiceErrore;
    private final LocalDateTime timestamp;

    public ErrorResponseDTO(String messaggio, String codiceErrore) {
        this.messaggio = messaggio;
        this.codiceErrore = codiceErrore;
        this.timestamp = LocalDateTime.now();
    }

    // getter, setter
    public String getMessaggio() { return messaggio; }
    public String getCodiceErrore() { return codiceErrore; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
