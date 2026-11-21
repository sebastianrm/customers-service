package com.yanki.customers.domain.model;

/**
 * Estado del cliente dentro de la plataforma Yanki.
 *
 * <p>Se persiste como {@code String} con el nombre de la constante.</p>
 */
public enum CustomerStatus {

    /** Cliente habilitado para operar. */
    ACTIVE,

    /** Cliente bloqueado por el banco. */
    BLOCKED,

    /** Cliente eliminado logicamente. */
    DELETED
}
