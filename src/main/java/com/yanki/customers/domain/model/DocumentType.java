package com.yanki.customers.domain.model;

/**
 * Tipo de documento de identidad aceptado por el banco.
 *
 * <p>Se persiste como {@code String} con el nombre de la constante.</p>
 */
public enum DocumentType {

    /** Documento Nacional de Identidad (Peru). */
    DNI,

    /** Carnet de Extranjeria. */
    CEX,

    /** Pasaporte. */
    PASSPORT,

    /** Registro Único de Contribuyentes (Peru). */
    RUC
}
