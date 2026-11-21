package com.yanki.customers.infrastructure.config;

import java.time.Duration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propiedades de la cache Redis del microservicio de clientes.
 *
 * <p>Los TTL son configurables por variables de entorno para ajustarlos sin
 * recompilar la aplicacion.</p>
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "yanki.cache")
public class CacheProperties {

    /** Tiempo de vida de los catalogos maestros. */
    private Duration catalogTtl = Duration.ofMinutes(30);

    /** Tiempo de vida de la cache de clientes por documento. */
    private Duration customerTtl = Duration.ofMinutes(10);
}
