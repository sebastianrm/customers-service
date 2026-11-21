package com.yanki.customers.infrastructure.cache.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.infrastructure.config.CacheProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Pruebas unitarias del adaptador Redis de clientes.
 */
@ExtendWith(MockitoExtension.class)
class CustomerCacheAdapterTest {

    private static final String DOCUMENT_KEY = "yanki:customers:document:DNI:70123456";

    @Mock
    private ReactiveStringRedisTemplate redisTemplate;

    @Mock
    private ReactiveValueOperations<String, String> valueOperations;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CacheProperties cacheProperties;

    @InjectMocks
    private CustomerCacheAdapter adapter;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(cacheProperties.getCatalogTtl()).thenReturn(java.time.Duration.ofMinutes(30));
        lenient().when(cacheProperties.getCustomerTtl()).thenReturn(java.time.Duration.ofMinutes(10));
    }

    @Test
    void shouldGetCatalogsFromCache() throws JsonProcessingException {
        CustomerCatalogs catalogs = CustomerCatalogs.defaults();
        when(valueOperations.get(CustomerCacheAdapter.CATALOGS_KEY))
                .thenReturn(Mono.just("{\"catalogs\":true}"));
        when(objectMapper.readValue("{\"catalogs\":true}", CustomerCatalogs.class))
                .thenReturn(catalogs);

        CustomerCatalogs result = adapter.getCatalogs().blockingGet();

        assertNotNull(result);
    }

    @Test
    void shouldReturnEmptyWhenCatalogsNotInCache() {
        when(valueOperations.get(CustomerCacheAdapter.CATALOGS_KEY))
                .thenReturn(Mono.empty());

        adapter.getCatalogs().test().assertNoValues().awaitDone(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Test
    void shouldPutCatalogsInCache() throws JsonProcessingException {
        CustomerCatalogs catalogs = CustomerCatalogs.defaults();
        when(objectMapper.writeValueAsString(catalogs)).thenReturn("{\"catalogs\":true}");
        when(valueOperations.set(eq(CustomerCacheAdapter.CATALOGS_KEY), eq("{\"catalogs\":true}"), any(java.time.Duration.class)))
                .thenReturn(Mono.just(true));

        adapter.putCatalogs(catalogs).blockingAwait();

        verify(valueOperations).set(eq(CustomerCacheAdapter.CATALOGS_KEY), eq("{\"catalogs\":true}"), any(java.time.Duration.class));
    }

    @Test
    void shouldGetCustomerByDocumentFromCache() throws JsonProcessingException {
        Customer customer = personalCustomer();
        when(valueOperations.get(DOCUMENT_KEY)).thenReturn(Mono.just("{\"id\":\"C-1\"}"));
        when(objectMapper.readValue("{\"id\":\"C-1\"}", Customer.class)).thenReturn(customer);

        Customer result = adapter.getCustomerByDocument(DocumentType.DNI, "70123456").blockingGet();

        assertEquals("C-1", result.getCustomerId());
    }

    @Test
    void shouldPutCustomerByDocumentInCache() throws JsonProcessingException {
        Customer customer = personalCustomer();
        when(objectMapper.writeValueAsString(customer)).thenReturn("{\"id\":\"C-1\"}");
        when(valueOperations.set(eq(DOCUMENT_KEY), eq("{\"id\":\"C-1\"}"), any(java.time.Duration.class)))
                .thenReturn(Mono.just(true));

        Customer result = adapter.putCustomerByDocument(customer).blockingGet();

        assertEquals("C-1", result.getCustomerId());
        verify(valueOperations).set(eq(DOCUMENT_KEY), eq("{\"id\":\"C-1\"}"), any(java.time.Duration.class));
    }

    @Test
    void shouldEvictCustomerByDocument() {
        when(redisTemplate.delete(DOCUMENT_KEY)).thenReturn(Mono.just(1L));

        adapter.evictCustomerByDocument(DocumentType.DNI, "70123456").blockingAwait();

        verify(redisTemplate).delete(DOCUMENT_KEY);
    }

    @Test
    void shouldReturnEmptyWhenRedisError() {
        when(valueOperations.get(CustomerCacheAdapter.CATALOGS_KEY))
                .thenReturn(Mono.error(new IllegalStateException("Redis no disponible")));

        adapter.getCatalogs().test().assertNoValues().awaitDone(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    // =====================================================================
    // Factory methods
    // =====================================================================

    private Customer personalCustomer() {
        return Customer.builder()
                .customerId("C-1")
                .type(CustomerType.PERSONAL)
                .profile(CustomerProfile.STANDARD)
                .status(CustomerStatus.ACTIVE)
                .documentType(DocumentType.DNI)
                .documentNumber("70123456")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@correo.com")
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
