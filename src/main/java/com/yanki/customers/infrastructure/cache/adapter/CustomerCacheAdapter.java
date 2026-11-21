package com.yanki.customers.infrastructure.cache.adapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.out.CustomerCachePort;
import com.yanki.customers.infrastructure.config.CacheProperties;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * Adaptador de salida que implementa la cache de clientes sobre Redis Reactivo.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerCacheAdapter implements CustomerCachePort {

    /** Prefijo de todas las claves de la cache del servicio. */
    public static final String KEY_PREFIX = "yanki:customers:";

    /** Clave de los catalogos maestros. */
    public static final String CATALOGS_KEY = KEY_PREFIX + "catalogs";

    /** Plantilla reactiva de Redis. */
    private final ReactiveStringRedisTemplate redisTemplate;

    /** Serializador JSON usado para guardar los objetos en Redis. */
    private final ObjectMapper objectMapper;

    /** Propiedades de TTL de la cache. */
    private final CacheProperties properties;

    @Override
    public Maybe<CustomerCatalogs> getCatalogs() {
        log.debug("Redis: consultando catalogos maestros desde cache");
        return Maybe.fromPublisher(
                redisTemplate.opsForValue().get(CATALOGS_KEY)
                        .switchIfEmpty(Mono.empty())
                        .flatMap(value -> {
                            log.debug("Redis: catalogos encontrados en cache");
                            return deserialize(value, CustomerCatalogs.class)
                                    .doOnSuccess(catalogs -> log.debug("Redis: catalogos deserializados exitosamente"));
                        })
                        .doOnError(error -> log.warn("No se pudo leer el catalogo maestro de Redis: {}", error.getMessage()))
                        .onErrorResume(error -> {
                            log.debug("Redis: cache MISS para catalogos maestros");
                            return Mono.empty();
                        })
        );
    }

    @Override
    public Completable putCatalogs(CustomerCatalogs catalogs) {
        log.info("Redis: guardando catalogos maestros en cache");
        return Completable.fromPublisher(
                serialize(catalogs)
                        .flatMap(value -> redisTemplate.opsForValue().set(CATALOGS_KEY, value, properties.getCatalogTtl())
                                .doOnSuccess(v -> log.info("Redis: catalogos maestros guardados en cache con TTL={}", properties.getCatalogTtl())))
                        .doOnError(error -> log.warn("No se pudo guardar el catalogo maestro en Redis: {}", error.getMessage()))
                        .onErrorResume(error -> Mono.empty())
                        .then()
        );
    }

    @Override
    public Maybe<Customer> getCustomerByDocument(DocumentType documentType, String documentNumber) {
        String key = documentKey(documentType, documentNumber);
        log.debug("Redis: consultando cliente desde cache: key={}", key);
        return Maybe.fromPublisher(
                redisTemplate.opsForValue().get(key)
                        .switchIfEmpty(Mono.empty())
                        .flatMap(value -> {
                            log.debug("Redis: cache HIT para cliente: key={}", key);
                            return deserialize(value, Customer.class)
                                    .doOnSuccess(customer -> log.debug("Redis: cliente deserializado desde cache: id={}, key={}", customer.getCustomerId(), key));
                        })
                        .doOnError(error -> log.warn("No se pudo leer la cache del cliente {}: {}", key, error.getMessage()))
                        .onErrorResume(error -> {
                            log.debug("Redis: cache MISS para cliente: key={}", key);
                            return Mono.empty();
                        })
        );
    }

    @Override
    public Maybe<Customer> putCustomerByDocument(Customer customer) {
        String key = documentKey(customer.getDocumentType(), customer.getDocumentNumber());
        log.debug("Redis: guardando cliente en cache: key={}, id={}", key, customer.getCustomerId());
        return Maybe.fromPublisher(
                serialize(customer)
                        .flatMap(value -> redisTemplate.opsForValue().set(key, value, properties.getCustomerTtl())
                                .doOnSuccess(v -> log.debug("Redis: cliente guardado en cache: key={}, TTL={}", key, properties.getCustomerTtl())))
                        .doOnError(error -> log.warn("No se pudo guardar la cache del cliente {}: {}", key, error.getMessage()))
                        .onErrorResume(error -> Mono.empty())
                        .then(Mono.just(customer))
        );
    }

    @Override
    public Completable evictCustomerByDocument(DocumentType documentType, String documentNumber) {
        String key = documentKey(documentType, documentNumber);
        log.debug("Redis: invalidando cache del cliente: key={}", key);
        return Completable.fromPublisher(
                redisTemplate.delete(key)
                        .doOnNext(result -> log.debug("Redis: cache del cliente {} invalidada, eliminado={}", key, result))
                        .doOnError(error -> log.warn("No se pudo invalidar la cache del cliente {}: {}", key, error.getMessage()))
                        .onErrorResume(error -> Mono.empty())
                        .then()
        );
    }

    /**
     * Construye la clave de cache de un cliente por documento.
     *
     * @param documentType tipo de documento
     * @param documentNumber numero de documento
     * @return clave de cache normalizada
     */
    private String documentKey(DocumentType documentType, String documentNumber) {
        String type = documentType == null ? "UNDEFINED" : documentType.name();
        String number = documentNumber == null ? "" : documentNumber.trim().toUpperCase(Locale.ROOT);
        return KEY_PREFIX + "document:" + type + ":" + number;
    }

    /**
     * Serializa un objeto a JSON.
     *
     * @param value objeto a serializar
     * @param <T> tipo del objeto
     * @return representacion JSON del objeto
     */
    private <T> Mono<String> serialize(T value) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(value))
                .doOnError(error -> log.error("Redis: error al serializar objeto a JSON: {}", error.getMessage(), error));
    }

    /**
     * Deserializa un JSON a un objeto del tipo indicado.
     *
     * @param value representacion JSON
     * @param type tipo esperado
     * @param <T> tipo del objeto
     * @return objeto deserializado
     */
    private <T> Mono<T> deserialize(String value, Class<T> type) {
        return Mono.fromCallable(() -> objectMapper.readValue(value, type))
                .doOnError(error -> log.error("Redis: error al deserializar JSON a {}: {}", type.getSimpleName(), error.getMessage(), error));
    }
}
