package com.yanki.customers.interfaces.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

/**
 * Cuerpo estandar de error de la API.
 *
 * @param timestamp momento en que se genero el error
 * @param status codigo de estado HTTP
 * @param error descripcion del estado HTTP
 * @param code codigo de negocio del error
 * @param message descripcion legible del error
 * @param path ruta que originó el error
 * @param details detalle adicional del error (por ejemplo, validaciones)
 */
@Schema(name = "ApiError", description = "Cuerpo estandar de error de la API")
public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String code,
        String message,
        String path,
        Map<String, Object> details) {

    /**
     * Construye un cuerpo de error con el detalle vacio.
     *
     * @param status codigo de estado HTTP
     * @param error descripcion del estado HTTP
     * @param code codigo de negocio del error
     * @param message descripcion legible del error
     * @param path ruta que originó el error
     * @return cuerpo de error
     */
    public static ApiError of(int status, String error, String code, String message, String path) {
        return new ApiError(Instant.now(), status, error, code, message, path, Map.of());
    }

    /**
     * Construye un cuerpo de error con detalle.
     *
     * @param status codigo de estado HTTP
     * @param error descripcion del estado HTTP
     * @param code codigo de negocio del error
     * @param message descripcion legible del error
     * @param path ruta que originó el error
     * @param details detalle adicional del error
     * @return cuerpo de error
     */
    public static ApiError of(int status, String error, String code, String message, String path,
                              Map<String, Object> details) {
        return new ApiError(Instant.now(), status, error, code, message, path,
                details == null ? Map.of() : details);
    }
}
