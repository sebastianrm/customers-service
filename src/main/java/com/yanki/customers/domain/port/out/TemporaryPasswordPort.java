package com.yanki.customers.domain.port.out;

/**
 * Puerto de salida para generar contrasenas temporales.
 */
public interface TemporaryPasswordPort {

    /**
     * Genera una contrasena temporal aleatoria.
     *
     * @return contrasena temporal
     */
    String generate();
}
