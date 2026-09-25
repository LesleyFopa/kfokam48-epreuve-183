package com.kfokam48.presence.exception;

import org.springframework.http.HttpStatus;

/**
 * Porte le code d'erreur stable et le statut HTTP attendus par le format
 * d'erreur imposé (api/contrat.yaml). Toute erreur métier doit passer par ici.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;
    private final String code;

    public ApiException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
