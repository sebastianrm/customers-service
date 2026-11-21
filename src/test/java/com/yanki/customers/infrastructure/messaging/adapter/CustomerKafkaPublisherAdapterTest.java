package com.yanki.customers.infrastructure.messaging.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yanki.customers.domain.event.CustomerDeletedEvent;
import com.yanki.customers.domain.event.CustomerRegisteredEvent;
import com.yanki.customers.domain.event.CustomerUpdatedEvent;
import com.yanki.customers.infrastructure.messaging.Topics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Pruebas unitarias del adaptador Kafka de eventos de clientes.
 */
@ExtendWith(MockitoExtension.class)
class CustomerKafkaPublisherAdapterTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private SendResult<String, Object> sendResult;

    @InjectMocks
    private CustomerKafkaPublisherAdapter adapter;

    @BeforeEach
    void setUp() {
        when(sendResult.getRecordMetadata()).thenReturn(null);
    }

    @Test
    void shouldPublishCustomerRegisteredEvent() {
        when(kafkaTemplate.send(eq(Topics.CUSTOMER_REGISTERED), eq("C-1"), any(CustomerRegisteredEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        CustomerRegisteredEvent event = new CustomerRegisteredEvent(
                "C-1", "juan.perez@correo.com", "juan.perez@correo.com",
                "TmpPass12345", List.of("ROLE_CUSTOMER"), "PERSONAL", "ACTIVE");

        adapter.publishRegistered(event).blockingAwait();

        ArgumentCaptor<CustomerRegisteredEvent> captor = ArgumentCaptor.forClass(CustomerRegisteredEvent.class);
        verify(kafkaTemplate).send(eq(Topics.CUSTOMER_REGISTERED), eq("C-1"), captor.capture());
        CustomerRegisteredEvent published = captor.getValue();
        assertEquals("C-1", published.customerId());
        assertEquals("juan.perez@correo.com", published.username());
        assertEquals("TmpPass12345", published.temporaryPassword());
        assertEquals(List.of("ROLE_CUSTOMER"), published.roles());
    }

    @Test
    void shouldPublishCustomerUpdatedEvent() {
        when(kafkaTemplate.send(eq(Topics.CUSTOMER_UPDATED), eq("C-1"), any(CustomerUpdatedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        LocalDateTime occurredAt = LocalDateTime.now();
        CustomerUpdatedEvent event = new CustomerUpdatedEvent(
                "C-1", "PERSONAL", "VIP", "ACTIVE", occurredAt);

        adapter.publishUpdated(event).blockingAwait();

        ArgumentCaptor<CustomerUpdatedEvent> captor = ArgumentCaptor.forClass(CustomerUpdatedEvent.class);
        verify(kafkaTemplate).send(eq(Topics.CUSTOMER_UPDATED), eq("C-1"), captor.capture());
        CustomerUpdatedEvent published = captor.getValue();
        assertEquals("C-1", published.customerId());
        assertEquals("PERSONAL", published.customerType());
        assertEquals("VIP", published.profile());
        assertEquals("ACTIVE", published.status());
        assertEquals(occurredAt, published.occurredAt());
    }

    @Test
    void shouldPublishCustomerDeletedEvent() {
        when(kafkaTemplate.send(eq(Topics.CUSTOMER_DELETED), eq("C-1"), any(CustomerDeletedEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        LocalDateTime occurredAt = LocalDateTime.now();
        CustomerDeletedEvent event = new CustomerDeletedEvent("C-1", occurredAt);

        adapter.publishDeleted(event).blockingAwait();

        ArgumentCaptor<CustomerDeletedEvent> captor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
        verify(kafkaTemplate).send(eq(Topics.CUSTOMER_DELETED), eq("C-1"), captor.capture());
        CustomerDeletedEvent published = captor.getValue();
        assertEquals("C-1", published.customerId());
        assertNotNull(published.occurredAt());
        assertEquals(occurredAt, published.occurredAt());
    }
}
