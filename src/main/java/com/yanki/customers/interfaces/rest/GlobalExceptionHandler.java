package com.yanki.customers.interfaces.rest;

import com.yanki.customers.domain.exception.BusinessException;
import com.yanki.customers.domain.exception.ErrorCodes;
import com.yanki.customers.interfaces.rest.dto.ApiError;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;

/**
 * Manejador global de excepciones de la API de clientes.
 *
 * <p>Todas las respuestas de error usan el mismo cuerpo {@link ApiError}.</p>
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Traduce los errores de negocio a su codigo HTTP.
     *
     * @param ex error de negocio
     * @param exchange contexto de la peticion
     * @return respuesta con el cuerpo de error
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiError> handleBusiness(BusinessException ex, ServerWebExchange exchange) {
        HttpStatus status = resolveBusinessStatus(ex.getCode());
        String path = path(exchange);
        log.warn("Error de negocio {} en {}: {}", ex.getCode(), path, ex.getMessage());
        log.debug("Detalles del error de negocio: code={}, message={}, path={}, stack={}",
                ex.getCode(), ex.getMessage(), path, ex.getClass().getName());
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), ex.getCode(), ex.getMessage(), path);
        return ResponseEntity.status(status).body(body);
    }

    /**
     * Traduce los errores de validacion del cuerpo de la peticion.
     *
     * @param ex error de validacion
     * @param exchange contexto de la peticion
     * @return respuesta con el cuerpo de error
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ApiError> handleValidation(WebExchangeBindException ex, ServerWebExchange exchange) {
        Map<String, Object> details = new LinkedHashMap<>();
        ex.getFieldErrors().forEach(fieldError -> details.put(fieldError.getField(), fieldError.getDefaultMessage()));
        String path = path(exchange);
        log.warn("Error de validacion en {}: {}", path, details);
        log.debug("Campos con error de validacion en {}: {} campos afectados", path, details.size());
        ApiError body = ApiError.of(HttpStatus.UNPROCESSABLE_ENTITY.value(), HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                "VALIDATION_ERROR", "La solicitud contiene datos invalidos", path, details);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    /**
     * Traduce los errores de entrada mal formada (por ejemplo, un enum invalido).
     *
     * @param ex error de entrada
     * @param exchange contexto de la peticion
     * @return respuesta con el cuerpo de error
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ApiError> handleInput(ServerWebInputException ex, ServerWebExchange exchange) {
        String path = path(exchange);
        log.warn("Error de entrada en {}: {} ({})", path, ex.getMessage(),
                ex.getMostSpecificCause().getMessage());
        log.debug("Causa raiz del error de entrada en {}: {}", path, ex.getMostSpecificCause().getClass().getName());
        ApiError body = ApiError.of(HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "INVALID_INPUT", "La solicitud no pudo ser procesada", path);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Conserva el codigo HTTP de las excepciones de estado de WebFlux.
     *
     * @param ex error de estado
     * @param exchange contexto de la peticion
     * @return respuesta con el cuerpo de error
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException ex, ServerWebExchange exchange) {
        int statusValue = ex.getStatusCode().value();
        HttpStatus resolved = HttpStatus.resolve(statusValue);
        String error = resolved != null ? resolved.getReasonPhrase() : ex.getStatusCode().toString();
        String message = ex.getReason() != null ? ex.getReason() : error;
        String path = path(exchange);
        log.warn("Error de estado HTTP en {}: status={}, reason={}", path, statusValue, message);
        log.debug("ResponseStatusException en {}: statusCode={}, reason={}", path, ex.getStatusCode(), ex.getReason());
        ApiError body = ApiError.of(statusValue, error, ex.getStatusCode().toString(), message, path);
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /**
     * Traduce los errores no controlados.
     *
     * @param ex error inesperado
     * @param exchange contexto de la peticion
     * @return respuesta con el cuerpo de error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, ServerWebExchange exchange) {
        String path = path(exchange);
        log.error("Error inesperado en {}: {}", path, ex.getMessage(), ex);
        log.error("Tipo de excepcion inesperada en {}: {}", path, ex.getClass().getName());
        ApiError body = ApiError.of(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), "INTERNAL_ERROR",
                "Ocurrio un error inesperado", path);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Determina el codigo HTTP de un error de negocio.
     *
     * @param code codigo de negocio
     * @return codigo HTTP correspondiente
     */
    private HttpStatus resolveBusinessStatus(String code) {
        return switch (code) {
            case ErrorCodes.CUSTOMER_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case ErrorCodes.CUSTOMER_ALREADY_EXISTS, ErrorCodes.CUSTOMER_ALREADY_DELETED -> HttpStatus.CONFLICT;
            default -> HttpStatus.UNPROCESSABLE_ENTITY;
        };
    }

    /**
     * Obtiene la ruta de la peticion que origino el error.
     *
     * @param exchange contexto de la peticion
     * @return ruta solicitada
     */
    private String path(ServerWebExchange exchange) {
        return exchange.getRequest().getPath().value();
    }
}
