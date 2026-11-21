package com.yanki.customers.domain.event;

import java.util.List;

/**
 * Evento publicado cuando un cliente es registrado en la plataforma.
 *
 * <p>Es consumido por {@code auth-service} para crear las credenciales de
 * acceso del cliente y por {@code cards-service} para actualizar la vista
 * local del perfil del cliente.</p>
 *
 * @param customerId identificador del cliente
 * @param username usuario derivado del correo electronico en minusculas
 * @param email correo electronico del cliente
 * @param temporaryPassword contrasena temporal generada para el cliente
 * @param roles roles asignados al cliente
 * @param customerType tipo de cliente (PERSONAL, CORPORATE)
 * @param status estado del cliente (ACTIVE, BLOCKED, DELETED)
 */
public record CustomerRegisteredEvent(
        String customerId,
        String username,
        String email,
        String temporaryPassword,
        List<String> roles,
        String customerType,
        String status) {
}
