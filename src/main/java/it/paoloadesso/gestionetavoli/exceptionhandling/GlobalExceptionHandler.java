package it.paoloadesso.gestionetavoli.exceptionhandling;

import it.paoloadesso.gestionetavoli.dto.ErrorResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Cattura gli errori RuntimeException (inclusi quelli del tuo Service)
     * Per esempio: throw new RuntimeException("Nessun prodotto da pagare")
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDTO> handleRuntimeException(RuntimeException e) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                e.getMessage(),
                "ERRORE_BUSINESS"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura gli errori che lancio io nei Service con ResponseStatusException.
     * Per esempio: throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tavolo non trovato")
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleBusinessErrors(ResponseStatusException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                ex.getReason(), // Il messaggio d'errore che ho scritto nel Service
                "ERRORE_BUSINESS"
        );
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    /**
     * Cattura errori di validazione sui DTO (quando @Valid fallisce)
     * Restituisce una mappa campo->errore invece di ErrorResponseDTO
     * perché è più utile per il frontend sapere quale campo è sbagliato
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        // Per ogni campo sbagliato nel DTO, metto campo->messaggio di errore
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Cattura errori di validazione sui parametri URL
     * Per esempio: @Positive Long idOrdine con valore negativo
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(ConstraintViolationException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Errore di validazione sui parametri",
                "VALIDAZIONE_PARAMETRI"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura parametri mancanti nelle richieste
     * Per esempio: GET /api/ordini senza parametro obbligatorio
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParameter(MissingServletRequestParameterException ex) {
        String message = getMissingParameterMessage(ex.getParameterName());

        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                "PARAMETRO_MANCANTE"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura errori di tipo sbagliato nei parametri
     * Per esempio: /api/ordini/abc invece di /api/ordini/123
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponseDTO> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = getTypeMismatchMessage(ex.getName());

        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                "TIPO_PARAMETRO_ERRATO"
        );
        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Cattura tutti gli errori che non ho previsto.
     * È il "paracadute di sicurezza"
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericErrors(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                "Si è verificato un errore interno del server",
                "ERRORE_INTERNO"
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Metodo di aiuto: quando manca un parametro, decide che messaggio mostrare.
     * Ho messo i messaggi specifici per i parametri che uso spesso.
     */
    private String getMissingParameterMessage(String parameterName) {
        return switch (parameterName) {
            case "idTavolo" -> "L'id del tavolo è obbligatorio";
            case "idOrdine" -> "L'id dell'ordine è obbligatorio";
            case "idProdotto" -> "L'id del prodotto è obbligatorio";
            case "categoria" -> "La categoria è obbligatoria";
            default -> "Il parametro '" + parameterName + "' è obbligatorio";
        };
    }

    /**
     * Metodo di aiuto: quando il tipo del parametro è sbagliato, decide che messaggio mostrare.
     * Per esempio: se mando "abc" invece di un numero per idTavolo.
     */
    private String getTypeMismatchMessage(String parameterName) {
        return switch (parameterName) {
            case "idTavolo" -> "L'id del tavolo deve essere un numero positivo";
            case "idOrdine" -> "L'id dell'ordine deve essere un numero positivo";
            case "idProdotto" -> "L'id del prodotto deve essere un numero positivo";
            default -> "Il parametro '" + parameterName + "' ha un formato non valido";
        };
    }
}
