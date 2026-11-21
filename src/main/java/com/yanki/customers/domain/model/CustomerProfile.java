package com.yanki.customers.domain.model;

/**
 * Perfil del cliente segun el tipo de cliente.
 *
 * <p>Se persiste como {@code String} con el nombre de la constante.</p>
 */
public enum CustomerProfile {

    /** Perfil estandar, valido para clientes personales y empresariales. */
    STANDARD,

    /** Perfil preferente, exclusivo de clientes personales. */
    VIP,

    /** Perfil de pequena y mediana empresa, exclusivo de clientes empresariales. */
    PYME
}
