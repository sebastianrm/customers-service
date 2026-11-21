package com.yanki.customers.domain.port.out;

import com.yanki.customers.domain.event.CustomerDeletedEvent;
import com.yanki.customers.domain.event.CustomerRegisteredEvent;
import com.yanki.customers.domain.event.CustomerUpdatedEvent;
import io.reactivex.rxjava3.core.Completable;

/**
 * Puerto de salida para publicar eventos de clientes hacia Kafka.
 */
public interface CustomerEventPublisherPort {

    /**
     * Publica el evento de cliente registrado.
     *
     * @param event evento a publicar
     * @return completable cuando la publicacion finaliza
     */
    Completable publishRegistered(CustomerRegisteredEvent event);

    /**
     * Publica el evento de cliente actualizado.
     *
     * @param event evento a publicar
     * @return completable cuando la publicacion finaliza
     */
    Completable publishUpdated(CustomerUpdatedEvent event);

    /**
     * Publica el evento de cliente eliminado.
     *
     * @param event evento a publicar
     * @return completable cuando la publicacion finaliza
     */
    Completable publishDeleted(CustomerDeletedEvent event);
}
