package com.yanki.customers.infrastructure.security.adapter;

import com.yanki.customers.domain.port.out.TemporaryPasswordPort;
import com.yanki.customers.infrastructure.security.TemporaryPasswordGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que implementa la generacion de contrasenas temporales.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TemporaryPasswordAdapter implements TemporaryPasswordPort {

    /** Generador de contrasenas temporales. */
    private final TemporaryPasswordGenerator generator;

    @Override
    public String generate() {
        try {
            String password = generator.generate();
            log.debug("Contrasena temporal generada exitosamente: longitud={}", password != null ? password.length() : 0);
            return password;
        } catch (Exception e) {
            log.error("Error al generar contrasena temporal: {}", e.getMessage(), e);
            throw e;
        }
    }
}
