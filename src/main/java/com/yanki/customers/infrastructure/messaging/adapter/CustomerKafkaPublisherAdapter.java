package com.yanki.customers.infrastructure.messaging.adapter;

import com.yanki.customers.domain.event.CustomerDeletedEvent;
import com.yanki.customers.domain.event.CustomerRegisteredEvent;
import com.yanki.customers.domain.event.CustomerUpdatedEvent;
import com.yanki.customers.domain.port.out.CustomerEventPublisherPort;
import com.yanki.customers.infrastructure.messaging.Topics;
import io.reactivex.rxjava3.core.Completable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * Adaptador de salida que publica eventos de clientes hacia Apache Kafka.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerKafkaPublisherAdapter implements CustomerEventPublisherPort {

    /** Plantilla de Kafka usada para enviar los eventos. */
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Completable publishRegistered(CustomerRegisteredEvent event) {
        log.info("Kafka: publicando evento de registro para cliente={}", event.customerId());
        log.debug("Kafka: clase del evento publicado: {}", event.getClass().getName());
        log.debug("Kafka: CustomerRegisteredEvent campos -> customerId={}, username={}, email={}, temporaryPassword={}, roles={}, customerType={}, status={}",
                event.customerId(), event.username(), event.email(), event.temporaryPassword(), event.roles(), event.customerType(), event.status());
        return send(Topics.CUSTOMER_REGISTERED, event.customerId(), event)
                .doOnComplete(() -> log.info("Kafka: evento de registro publicado exitosamente para cliente={}", event.customerId()))
                .doOnError(error -> log.error("Kafka: error al publicar evento de registro para cliente={}: {}",
                        event.customerId(), error.getMessage(), error));
    }

    @Override
    public Completable publishUpdated(CustomerUpdatedEvent event) {
        log.info("Kafka: publicando evento de actualizacion para cliente={}", event.customerId());
        log.debug("Kafka: clase del evento publicado: {}", event.getClass().getName());
        log.debug("Kafka: CustomerUpdatedEvent campos -> customerId={}, customerType={}, profile={}, status={}, occurredAt={}",
                event.customerId(), event.customerType(), event.profile(), event.status(), event.occurredAt());
        return send(Topics.CUSTOMER_UPDATED, event.customerId(), event)
                .doOnComplete(() -> log.info("Kafka: evento de actualizacion publicado exitosamente para cliente={}", event.customerId()))
                .doOnError(error -> log.error("Kafka: error al publicar evento de actualizacion para cliente={}: {}",
                        event.customerId(), error.getMessage(), error));
    }

    @Override
    public Completable publishDeleted(CustomerDeletedEvent event) {
        log.info("Kafka: publicando evento de eliminacion para cliente={}", event.customerId());
        log.debug("Kafka: clase del evento publicado: {}", event.getClass().getName());
        log.debug("Kafka: CustomerDeletedEvent campos -> customerId={}, occurredAt={}",
                event.customerId(), event.occurredAt());
        return send(Topics.CUSTOMER_DELETED, event.customerId(), event)
                .doOnComplete(() -> log.info("Kafka: evento de eliminacion publicado exitosamente para cliente={}", event.customerId()))
                .doOnError(error -> log.error("Kafka: error al publicar evento de eliminacion para cliente={}: {}",
                        event.customerId(), error.getMessage(), error));
    }

    /**
     * Envia un evento al topic indicado.
     *
     * @param topic topic de destino
     * @param key clave del mensaje (identificador del cliente)
     * @param event payload del evento
     * @return completable que se completa cuando el evento fue enviado
     */
    private Completable send(String topic, String key, Object event) {
        log.debug("Kafka: enviando mensaje a topic={}, key={}", topic, key);
        return Completable.fromPublisher(Mono.fromFuture(() -> kafkaTemplate.send(topic, key, event))
                .doOnNext(result -> {
                    org.apache.kafka.clients.producer.RecordMetadata metadata = result.getRecordMetadata();
                    if (metadata != null) {
                        log.debug("Kafka: mensaje enviado a topic={}, key={}, partition={}, offset={}",
                                topic, key, metadata.partition(), metadata.offset());
                    } else {
                        log.debug("Kafka: mensaje enviado a topic={}, key={}", topic, key);
                    }
                })
                .doOnError(error -> log.error("Kafka: error al enviar mensaje a topic={}, key={}: {}",
                        topic, key, error.getMessage(), error))
                .then());
    }
}
