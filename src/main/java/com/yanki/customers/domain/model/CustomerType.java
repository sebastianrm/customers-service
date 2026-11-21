package com.yanki.customers.domain.model;

/**
 * Tipo de cliente del banco.
 *
 * <p>Se persiste como {@code String} con el nombre de la constante
 * (por ejemplo {@code PERSONAL} o {@code ENTERPRISE}).</p>
 */
public enum CustomerType {

    /** Cliente persona natural. */
    PERSONAL,

    /** Cliente persona juridica (empresa). */
    ENTERPRISE
}
