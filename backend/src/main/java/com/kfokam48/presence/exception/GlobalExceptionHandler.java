package com.kfokam48.presence.exception;

import com.kfokam48.presence.dto.ErreurDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Centralise toutes les erreurs vers le format imposé (B4). Aucune stack
 * trace, aucun corps vide, aucune page d'erreur Spring par défaut ne doit
 * jamais atteindre le client.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErreurDto> handleApiException(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ErreurDto(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurDto> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getField() + " : " + f.getDefaultMessage())
                .orElse("Requête invalide.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErreurDto("CHAMP_INVALIDE", message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErreurDto> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErreurDto("PARAMETRE_MANQUANT", "Paramètre requis manquant : " + ex.getParameterName()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErreurDto> handleUnexpected(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErreurDto("ERREUR_INTERNE", "Une erreur inattendue est survenue."));
    }
}
