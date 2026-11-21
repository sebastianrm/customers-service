package com.yanki.customers.domain.event;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando un cliente es eliminado logicamente.
 *
 * <p>Los microservicios consumidores bloquean los productos asociados al
 * cliente eliminado.</p>
 *
 * @param customerId identificador del cliente eliminado
 * @param occurredAt momento en que ocurrio la eliminacion
 */
public record CustomerDeletedEvent(
        String customerId,
        LocalDateTime occurredAt) {
}
