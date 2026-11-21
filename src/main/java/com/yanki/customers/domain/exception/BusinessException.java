package com.yanki.customers.domain.exception;

/**
 * Error de negocio de la plataforma Yanki.
 *
 * <p>Transporta un codigo estable (por ejemplo {@code CUSTOMER_ALREADY_EXISTS})
 * que el manejador global de excepciones translate a una respuesta HTTP.</p>
 */
public class BusinessException extends RuntimeException {

    /** Codigo de negocio estable del error. */
    private final String code;

    /**
     * Crea una excepcion de negocio.
     *
     * @param code codigo de negocio estable
     * @param message descripcion legible del error
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * Obtiene el codigo de negocio del error.
     *
     * @return codigo de negocio estable
     */
    public String getCode() {
        return code;
    }
}
