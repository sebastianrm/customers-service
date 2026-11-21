package com.yanki.customers.infrastructure.security;

import java.security.SecureRandom;
import org.springframework.stereotype.Component;

/**
 * Generador de contrasenas temporales para los clientes registrados.
 *
 * <p>Usa {@link SecureRandom} y un alfabeto sin caracteres ambiguos para
 * evitar confusiones en la lectura de la credencial.</p>
 */
@Component
public class TemporaryPasswordGenerator {

    /** Alfabeto utilizado para construir la contrasena. */
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

    /** Longitud de la contrasena temporal. */
    public static final int LENGTH = 12;

    /** Fuente de numeros aleatorios criptograficamente segura. */
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Genera una contrasena temporal aleatoria de doce caracteres.
     *
     * @return contrasena temporal de doce caracteres
     */
    public String generate() {
        StringBuilder password = new StringBuilder(LENGTH);
        for (int index = 0; index < LENGTH; index++) {
            password.append(ALPHABET.charAt(secureRandom.nextInt(ALPHABET.length())));
        }
        return password.toString();
    }
}
