package com.yanki.customers.infrastructure.persistence.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.service.CustomerFilter;
import com.yanki.customers.infrastructure.persistence.document.CustomerDocument;
import com.yanki.customers.infrastructure.persistence.mapper.CustomerDocumentMapper;
import com.yanki.customers.infrastructure.persistence.repository.SpringDataCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pruebas unitarias del adaptador MongoDB de clientes.
 */
@ExtendWith(MockitoExtension.class)
class CustomerMongoAdapterTest {

    @Mock
    private SpringDataCustomerRepository repository;

    @Mock
    private ReactiveMongoTemplate mongoTemplate;

    @InjectMocks
    private CustomerMongoAdapter adapter;

    @Test
    void shouldFindCustomerById() {
        CustomerDocument doc = customerDocument();
        when(repository.findById("C-1")).thenReturn(Mono.just(doc));

        Customer customer = adapter.findById("C-1").blockingGet();

        assertEquals("C-1", customer.getCustomerId());
        assertEquals("juan.perez@correo.com", customer.getEmail());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        when(repository.findById("missing")).thenReturn(Mono.empty());

        adapter.findById("missing").test().assertNoValues().awaitDone(5, java.util.concurrent.TimeUnit.SECONDS);
    }

    @Test
    void shouldFindCustomerByDocumentNumber() {
        CustomerDocument doc = customerDocument();
        when(repository.findByDocumentNumber("70123456")).thenReturn(Mono.just(doc));

        Customer customer = adapter.findByDocumentNumber("70123456").blockingGet();

        assertEquals("70123456", customer.getDocumentNumber());
    }

    @Test
    void shouldFindCustomerByDocumentTypeAndNumber() {
        CustomerDocument doc = customerDocument();
        when(repository.findByDocumentTypeAndDocumentNumber("DNI", "70123456"))
                .thenReturn(Mono.just(doc));

        Customer customer = adapter.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "70123456")
                .blockingGet();

        assertEquals(DocumentType.DNI, customer.getDocumentType());
        assertEquals("70123456", customer.getDocumentNumber());
    }

    @Test
    void shouldSaveCustomer() {
        Customer customer = personalCustomer();
        CustomerDocument savedDoc = CustomerDocumentMapper.toDocument(customer);
        savedDoc.setCustomerId("C-1");
        when(repository.save(any(CustomerDocument.class))).thenReturn(Mono.just(savedDoc));

        Customer saved = adapter.save(customer).blockingGet();

        assertEquals("C-1", saved.getCustomerId());
        ArgumentCaptor<CustomerDocument> captor = ArgumentCaptor.forClass(CustomerDocument.class);
        verify(repository).save(captor.capture());
        assertEquals("70123456", captor.getValue().getDocumentNumber());
    }

    @Test
    void shouldSearchCustomersWithFilters() {
        CustomerFilter filter = CustomerFilter.of(CustomerType.PERSONAL, CustomerProfile.VIP,
                CustomerStatus.ACTIVE, 0, 20);
        CustomerDocument doc = customerDocument();
        when(mongoTemplate.count(any(Query.class), eq(CustomerDocument.class)))
                .thenReturn(Mono.just(1L));
        when(mongoTemplate.find(any(Query.class), eq(CustomerDocument.class)))
                .thenReturn(Flux.just(doc));

        CustomerPage page = adapter.search(filter).blockingGet();

        assertNotNull(page);
        assertEquals(1, page.content().size());
        assertEquals(1L, page.totalElements());
    }

    // =====================================================================
    // Factory methods
    // =====================================================================

    private CustomerDocument customerDocument() {
        return CustomerDocument.builder()
                .customerId("C-1")
                .type("PERSONAL")
                .profile("STANDARD")
                .status("ACTIVE")
                .documentType("DNI")
                .documentNumber("70123456")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@correo.com")
                .phone("+51987654321")
                .address("Av. Javier Prado 4200")
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }

    private Customer personalCustomer() {
        return Customer.builder()
                .type(CustomerType.PERSONAL)
                .profile(CustomerProfile.STANDARD)
                .status(CustomerStatus.ACTIVE)
                .documentType(DocumentType.DNI)
                .documentNumber("70123456")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@correo.com")
                .phone("+51987654321")
                .address("Av. Javier Prado 4200")
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
