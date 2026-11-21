package com.yanki.customers.domain.exception;

/**
 * Codigos de error de negocio del microservicio de clientes.
 *
 * <p>Son estables: el manejador global de excepciones los usa para elegir el
 * codigo HTTP de la respuesta.</p>
 */
public final class ErrorCodes {

    /** Ya existe un cliente registrado con el mismo numero de documento. */
    public static final String CUSTOMER_ALREADY_EXISTS = "CUSTOMER_ALREADY_EXISTS";

    /** El perfil no es compatible con el tipo de cliente. */
    public static final String INVALID_CUSTOMER_PROFILE = "INVALID_CUSTOMER_PROFILE";

    /** Los datos del cliente no cumplen las reglas del tipo de cliente. */
    public static final String INVALID_CUSTOMER_DATA = "INVALID_CUSTOMER_DATA";

    /** El tipo de documento es obligatorio y debe ser uno de los soportados. */
    public static final String INVALID_DOCUMENT_TYPE = "INVALID_DOCUMENT_TYPE";

    /** El estado solicitado no puede asignarse mediante una actualizacion. */
    public static final String INVALID_CUSTOMER_STATUS = "INVALID_CUSTOMER_STATUS";

    /** No existe un cliente con el identificador indicado. */
    public static final String CUSTOMER_NOT_FOUND = "CUSTOMER_NOT_FOUND";

    /** El cliente ya fue eliminado logicamente. */
    public static final String CUSTOMER_ALREADY_DELETED = "CUSTOMER_ALREADY_DELETED";

    /**
     * Constructor privado para evitar la instanciacion de la clase utilitaria.
     */
    private ErrorCodes() {
    }
}
