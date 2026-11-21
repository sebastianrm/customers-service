package com.yanki.customers.infrastructure.messaging;

/**
 * Topics de Kafka publicados por el microservicio de clientes.
 *
 * <p>El contrato completo de los payloads esta documentado en
 * {@code docs/EVENTOS.md} de la plataforma Yanki.</p>
 */
public final class Topics {

    /** Evento de cliente registrado. */
    public static final String CUSTOMER_REGISTERED = "yanki.customer.registered.v1";

    /** Evento de cliente actualizado. */
    public static final String CUSTOMER_UPDATED = "yanki.customer.updated.v1";

    /** Evento de cliente eliminado logicamente. */
    public static final String CUSTOMER_DELETED = "yanki.customer.deleted.v1";

    /**
     * Constructor privado para evitar la instanciacion de la clase utilitaria.
     */
    private Topics() {
    }
}
