package com.yanki.customers.domain.event;

import java.time.LocalDateTime;

/**
 * Evento publicado cuando se actualiza un cliente.
 *
 * <p>Permite a los demas microservicios refrescar la vista local del
 * cliente (tipo, perfil y estado).</p>
 *
 * @param customerId identificador del cliente
 * @param customerType tipo de cliente (PERSONAL o ENTERPRISE)
 * @param profile perfil del cliente (STANDARD, VIP o PYME)
 * @param status estado del cliente (ACTIVE, BLOCKED o DELETED)
 * @param occurredAt momento en que ocurrio el cambio
 */
public record CustomerUpdatedEvent(
        String customerId,
        String customerType,
        String profile,
        String status,
        LocalDateTime occurredAt) {
}
