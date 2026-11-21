package com.yanki.customers.infrastructure.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Pruebas unitarias del generador de contrasenas temporales.
 */
class TemporaryPasswordGeneratorTest {

    private final TemporaryPasswordGenerator generator = new TemporaryPasswordGenerator();

    @Test
    void shouldGeneratePasswordOfTwelveCharacters() {
        String password = generator.generate();

        assertEquals(TemporaryPasswordGenerator.LENGTH, password.length());
        assertEquals(12, password.length());
        assertTrue(password.matches("[A-Za-z0-9]+"));
    }

    @Test
    void shouldGenerateDifferentPasswordsOnEachCall() {
        assertNotEquals(generator.generate(), generator.generate());
    }
}
