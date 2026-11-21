package com.yanki.customers.domain.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yanki.customers.domain.event.CustomerDeletedEvent;
import com.yanki.customers.domain.event.CustomerRegisteredEvent;
import com.yanki.customers.domain.event.CustomerUpdatedEvent;
import com.yanki.customers.domain.exception.BusinessException;
import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.out.CustomerCachePort;
import com.yanki.customers.domain.port.out.CustomerEventPublisherPort;
import com.yanki.customers.domain.port.out.CustomerPersistencePort;
import com.yanki.customers.domain.port.out.TemporaryPasswordPort;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias del servicio de dominio de clientes.
 * Cubre los casos de prueba CP-001 a CP-019 definidos en test-cases/CASOS_PRUEBA.md.
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerPersistencePort persistencePort;

    @Mock
    private CustomerEventPublisherPort eventPublisherPort;

    @Mock
    private CustomerCachePort cachePort;

    @Mock
    private TemporaryPasswordPort passwordPort;

    @InjectMocks
    private CustomerService customerService;

    // =====================================================================
    // CP-001: Alta de cliente personal exitosa (STANDARD)
    // =====================================================================

    @Test
    void cp001_shouldCreatePersonalCustomerStandard() {
        when(persistencePort.findByDocumentNumber("70123456")).thenReturn(Maybe.empty());
        when(passwordPort.generate()).thenReturn("TmpPass12345");
        when(persistencePort.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            Customer withId = Customer.builder()
                    .customerId("C-1")
                    .type(saved.getType())
                    .profile(saved.getProfile())
                    .status(saved.getStatus())
                    .documentType(saved.getDocumentType())
                    .documentNumber(saved.getDocumentNumber())
                    .businessName(saved.getBusinessName())
                    .firstName(saved.getFirstName())
                    .lastName(saved.getLastName())
                    .email(saved.getEmail())
                    .phone(saved.getPhone())
                    .address(saved.getAddress())
                    .birthDate(saved.getBirthDate())
                    .active(saved.isActive())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();
            return Single.just(withId);
        });
        when(eventPublisherPort.publishRegistered(any(CustomerRegisteredEvent.class)))
                .thenReturn(Completable.complete());

        CustomerResult result = customerService.execute(personalStandardCommand()).blockingGet();

        assertEquals("C-1", result.customer().getCustomerId());
        assertEquals(CustomerStatus.ACTIVE, result.customer().getStatus());
        assertEquals(CustomerProfile.STANDARD, result.customer().getProfile());
        assertEquals("juan.perez@correo.com", result.customer().getEmail());
        assertEquals("70123456", result.customer().getDocumentNumber());
        assertTrue(result.customer().isActive());
        assertEquals("TmpPass12345", result.temporaryPassword());
        assertNotNull(result.customer().getCreatedAt());
        assertNotNull(result.customer().getUpdatedAt());

        ArgumentCaptor<CustomerRegisteredEvent> captor = ArgumentCaptor.forClass(CustomerRegisteredEvent.class);
        verify(eventPublisherPort).publishRegistered(captor.capture());
        CustomerRegisteredEvent event = captor.getValue();
        assertEquals("C-1", event.customerId());
        assertEquals("juan.perez@correo.com", event.username());
        assertEquals(List.of("ROLE_CUSTOMER"), event.roles());
    }

    // =====================================================================
    // CP-002: Alta de cliente personal exitosa (VIP)
    // =====================================================================

    @Test
    void cp002_shouldCreatePersonalCustomerVip() {
        when(persistencePort.findByDocumentNumber("70123456")).thenReturn(Maybe.empty());
        when(passwordPort.generate()).thenReturn("TmpPass12345");
        when(persistencePort.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            Customer withId = Customer.builder()
                    .customerId("C-1")
                    .type(saved.getType())
                    .profile(saved.getProfile())
                    .status(saved.getStatus())
                    .documentType(saved.getDocumentType())
                    .documentNumber(saved.getDocumentNumber())
                    .businessName(saved.getBusinessName())
                    .firstName(saved.getFirstName())
                    .lastName(saved.getLastName())
                    .email(saved.getEmail())
                    .phone(saved.getPhone())
                    .address(saved.getAddress())
                    .birthDate(saved.getBirthDate())
                    .active(saved.isActive())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();
            return Single.just(withId);
        });
        when(eventPublisherPort.publishRegistered(any(CustomerRegisteredEvent.class)))
                .thenReturn(Completable.complete());

        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.VIP, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "Juan.Perez@Correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);

        CustomerResult result = customerService.execute(command).blockingGet();

        assertEquals(CustomerProfile.VIP, result.customer().getProfile());
        assertEquals(CustomerType.PERSONAL, result.customer().getType());

        ArgumentCaptor<CustomerRegisteredEvent> captor = ArgumentCaptor.forClass(CustomerRegisteredEvent.class);
        verify(eventPublisherPort).publishRegistered(captor.capture());
        assertEquals(List.of("ROLE_CUSTOMER"), captor.getValue().roles());
    }

    // =====================================================================
    // CP-003: Alta de cliente empresarial exitosa (ENTERPRISE/PYME)
    // =====================================================================

    @Test
    void cp003_shouldCreateEnterpriseCustomerPyme() {
        when(persistencePort.findByDocumentNumber("20123456789")).thenReturn(Maybe.empty());
        when(passwordPort.generate()).thenReturn("TmpPass98765");
        when(persistencePort.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            Customer withId = Customer.builder()
                    .customerId("C-2")
                    .type(saved.getType())
                    .profile(saved.getProfile())
                    .status(saved.getStatus())
                    .documentType(saved.getDocumentType())
                    .documentNumber(saved.getDocumentNumber())
                    .businessName(saved.getBusinessName())
                    .firstName(saved.getFirstName())
                    .lastName(saved.getLastName())
                    .email(saved.getEmail())
                    .phone(saved.getPhone())
                    .address(saved.getAddress())
                    .birthDate(saved.getBirthDate())
                    .active(saved.isActive())
                    .createdAt(saved.getCreatedAt())
                    .updatedAt(saved.getUpdatedAt())
                    .build();
            return Single.just(withId);
        });
        when(eventPublisherPort.publishRegistered(any(CustomerRegisteredEvent.class)))
                .thenReturn(Completable.complete());

        CustomerCommand command = new CustomerCommand(
                CustomerType.ENTERPRISE, CustomerProfile.PYME, DocumentType.RUC, "20123456789",
                "Acme S.A.C.", null, null, "contacto@acme.com", "+51987654321",
                "Av. Arequipa 1500", null, null, null);

        CustomerResult result = customerService.execute(command).blockingGet();

        assertEquals(CustomerType.ENTERPRISE, result.customer().getType());
        assertEquals(CustomerProfile.PYME, result.customer().getProfile());
        assertEquals("Acme S.A.C.", result.customer().getBusinessName());

        ArgumentCaptor<CustomerRegisteredEvent> captor = ArgumentCaptor.forClass(CustomerRegisteredEvent.class);
        verify(eventPublisherPort).publishRegistered(captor.capture());
        assertEquals(List.of("ROLE_CUSTOMER", "ROLE_ENTERPRISE"), captor.getValue().roles());
    }

    // =====================================================================
    // CP-004: Alta rechazada por documento duplicado
    // =====================================================================

    @Test
    void cp004_shouldRejectDuplicateDocument() {
        when(persistencePort.findByDocumentNumber("70123456")).thenReturn(Maybe.just(existingCustomer()));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.execute(personalStandardCommand()).blockingGet());

        assertEquals("CUSTOMER_ALREADY_EXISTS", ex.getCode());
        verify(persistencePort, never()).save(any(Customer.class));
        verify(eventPublisherPort, never()).publishRegistered(any());
    }

    // =====================================================================
    // CP-005: Alta rechazada: PERSONAL sin firstName/lastName
    // =====================================================================

    @Test
    void cp005_shouldRejectPersonalWithoutNames() {
        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.STANDARD, DocumentType.DNI, "70123456",
                null, null, null, "juan@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.execute(command).blockingGet());

        assertEquals("INVALID_CUSTOMER_DATA", ex.getCode());
        verify(persistencePort, never()).save(any(Customer.class));
    }

    // =====================================================================
    // CP-006: Alta rechazada: ENTERPRISE sin businessName
    // =====================================================================

    @Test
    void cp006_shouldRejectEnterpriseWithoutBusinessName() {
        CustomerCommand command = new CustomerCommand(
                CustomerType.ENTERPRISE, CustomerProfile.PYME, DocumentType.RUC, "20123456789",
                null, null, null, "contacto@acme.com", "+51987654321",
                "Av. Arequipa 1500", null, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.execute(command).blockingGet());

        assertEquals("INVALID_CUSTOMER_DATA", ex.getCode());
        verify(persistencePort, never()).save(any(Customer.class));
    }

    // =====================================================================
    // CP-007: Alta rechazada: Perfil VIP en ENTERPRISE
    // =====================================================================

    @Test
    void cp007_shouldRejectVipProfileOnEnterprise() {
        CustomerCommand command = new CustomerCommand(
                CustomerType.ENTERPRISE, CustomerProfile.VIP, DocumentType.RUC, "20123456789",
                "Acme S.A.C.", null, null, "contacto@acme.com", "+51987654321",
                "Av. Arequipa 1500", null, null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.execute(command).blockingGet());

        assertEquals("INVALID_CUSTOMER_PROFILE", ex.getCode());
    }

    // =====================================================================
    // CP-008: Alta rechazada: Perfil PYME en PERSONAL
    // =====================================================================

    @Test
    void cp008_shouldRejectPymeProfileOnPersonal() {
        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.PYME, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "juan@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.execute(command).blockingGet());

        assertEquals("INVALID_CUSTOMER_PROFILE", ex.getCode());
    }

    // =====================================================================
    // CP-009: Consulta de cliente por ID exitosa
    // =====================================================================

    @Test
    void cp009_shouldFindCustomerById() {
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(existingCustomer()));

        Customer customer = customerService.findById("C-1").blockingGet();

        assertEquals("C-1", customer.getCustomerId());
        assertEquals("juan.perez@correo.com", customer.getEmail());
    }

    // =====================================================================
    // CP-010: Consulta de cliente por documento (Redis Cache HIT)
    // =====================================================================

    @Test
    void cp010_shouldFindCustomerByDocumentFromCache() {
        when(cachePort.getCustomerByDocument(DocumentType.DNI, "70123456"))
                .thenReturn(Maybe.just(existingCustomer()));

        Customer customer = customerService.findByDocument(DocumentType.DNI, "70123456").blockingGet();

        assertEquals("C-1", customer.getCustomerId());
    }

    // =====================================================================
    // CP-011: Consulta de cliente por documento (Redis Cache MISS -> MongoDB)
    // =====================================================================

    @Test
    void cp011_shouldFindCustomerByDocumentFromDatabaseWhenCacheMiss() {
        when(cachePort.getCustomerByDocument(DocumentType.DNI, "70123456")).thenReturn(Maybe.empty());
        when(persistencePort.findByDocumentTypeAndDocumentNumber(DocumentType.DNI, "70123456"))
                .thenReturn(Maybe.just(existingCustomer()));
        when(cachePort.putCustomerByDocument(any(Customer.class)))
                .thenReturn(Maybe.just(existingCustomer()));

        Customer customer = customerService.findByDocument(DocumentType.DNI, "70123456").blockingGet();

        assertEquals("C-1", customer.getCustomerId());
        verify(cachePort).putCustomerByDocument(any(Customer.class));
    }

    // =====================================================================
    // CP-012: Actualización de cliente exitosa (cambio perfil a VIP)
    // =====================================================================

    @Test
    void cp012_shouldUpdateCustomerProfileToVip() {
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(existingCustomer()));
        when(persistencePort.save(any(Customer.class))).thenAnswer(invocation ->
                Single.just(invocation.getArgument(0)));
        when(eventPublisherPort.publishUpdated(any(CustomerUpdatedEvent.class)))
                .thenReturn(Completable.complete());
        when(cachePort.evictCustomerByDocument(any(DocumentType.class), anyString()))
                .thenReturn(Completable.complete());

        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.VIP, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "juan.perez@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), CustomerStatus.ACTIVE, true);

        Customer updated = customerService.executeUpdate("C-1", command).blockingGet();

        assertEquals(CustomerProfile.VIP, updated.getProfile());
        assertEquals(CustomerStatus.ACTIVE, updated.getStatus());
        assertNotNull(updated.getUpdatedAt());

        ArgumentCaptor<CustomerUpdatedEvent> captor = ArgumentCaptor.forClass(CustomerUpdatedEvent.class);
        verify(eventPublisherPort).publishUpdated(captor.capture());
        assertEquals("C-1", captor.getValue().customerId());
        assertEquals("VIP", captor.getValue().profile());
    }

    // =====================================================================
    // CP-015: Actualización rechazada: intento asignar status=DELETED
    // =====================================================================

    @Test
    void cp015_shouldRejectStatusDeletedOnUpdate() {
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(existingCustomer()));

        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.STANDARD, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "juan.perez@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), CustomerStatus.DELETED, true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.executeUpdate("C-1", command).blockingGet());

        assertEquals("INVALID_CUSTOMER_STATUS", ex.getCode());
    }

    // =====================================================================
    // CP-016: Baja lógica de cliente exitosa
    // =====================================================================

    @Test
    void cp016_shouldDeleteCustomerLogically() {
        Customer existing = existingCustomer();
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(existing));
        when(persistencePort.save(any(Customer.class))).thenAnswer(invocation ->
                Single.just(invocation.getArgument(0)));
        when(eventPublisherPort.publishDeleted(any(CustomerDeletedEvent.class)))
                .thenReturn(Completable.complete());
        when(cachePort.evictCustomerByDocument(DocumentType.DNI, "70123456"))
                .thenReturn(Completable.complete());

        customerService.executeDelete("C-1").blockingAwait();

        assertEquals(CustomerStatus.DELETED, existing.getStatus());
        assertFalse(existing.isActive());
        assertNotNull(existing.getUpdatedAt());

        ArgumentCaptor<CustomerDeletedEvent> captor = ArgumentCaptor.forClass(CustomerDeletedEvent.class);
        verify(eventPublisherPort).publishDeleted(captor.capture());
        assertEquals("C-1", captor.getValue().customerId());
    }

    // =====================================================================
    // CP-017: Baja lógica rechazada: cliente ya DELETED
    // =====================================================================

    @Test
    void cp017_shouldRejectDeleteAlreadyDeletedCustomer() {
        Customer deleted = Customer.builder()
                .customerId("C-1")
                .type(CustomerType.PERSONAL)
                .profile(CustomerProfile.STANDARD)
                .status(CustomerStatus.DELETED)
                .documentType(DocumentType.DNI)
                .documentNumber("70123456")
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@correo.com")
                .active(false)
                .build();
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(deleted));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.executeDelete("C-1").blockingAwait());

        assertEquals("CUSTOMER_ALREADY_DELETED", ex.getCode());
        verify(persistencePort, never()).save(any(Customer.class));
    }

    // =====================================================================
    // CP-018: Listado de clientes con filtros y paginación
    // =====================================================================

    @Test
    void cp018_shouldListCustomersWithFilters() {
        CustomerFilter filter = CustomerFilter.of(CustomerType.PERSONAL, CustomerProfile.VIP,
                CustomerStatus.ACTIVE, 0, 20);
        when(persistencePort.search(filter))
                .thenReturn(Single.just(CustomerPage.of(List.of(existingCustomer()), 0, 20, 1)));

        CustomerPage page = customerService.findAll(filter).blockingGet();

        assertEquals(1, page.content().size());
        assertEquals(0, page.page());
        assertEquals(20, page.size());
        assertEquals(1L, page.totalElements());
        assertEquals(1, page.totalPages());
    }

    // =====================================================================
    // CP-019: Consulta de catálogos maestros (Redis Cache)
    // =====================================================================

    @Test
    void cp019_shouldGetCatalogsFromCache() {
        CustomerCatalogs catalogs = CustomerCatalogs.defaults();
        when(cachePort.getCatalogs()).thenReturn(Maybe.just(catalogs));

        CustomerCatalogs result = customerService.getCatalogs().blockingGet();

        assertNotNull(result);
        verify(cachePort).getCatalogs();
    }

    // =====================================================================
    // Pruebas adicionales de validación
    // =====================================================================

    @Test
    void shouldFailWhenCustomerDoesNotExist() {
        when(persistencePort.findById("missing")).thenReturn(Maybe.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.findById("missing").blockingGet());

        assertEquals("CUSTOMER_NOT_FOUND", ex.getCode());
    }

    @Test
    void shouldFailWhenUpdatedDocumentBelongsToAnotherCustomer() {
        when(persistencePort.findById("C-1")).thenReturn(Maybe.just(existingCustomer()));
        when(persistencePort.findByDocumentNumber("70999999")).thenReturn(Maybe.just(existingCustomer()));

        CustomerCommand command = new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.STANDARD, DocumentType.DNI, "70999999",
                null, "Juan", "Perez", "juan.perez@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> customerService.executeUpdate("C-1", command).blockingGet());

        assertEquals("CUSTOMER_ALREADY_EXISTS", ex.getCode());
    }

    // =====================================================================
    // Factory methods
    // =====================================================================

    private CustomerCommand personalStandardCommand() {
        return new CustomerCommand(
                CustomerType.PERSONAL, CustomerProfile.STANDARD, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "Juan.Perez@Correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);
    }

    private Customer existingCustomer() {
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
                .phone("+51987654321")
                .address("Av. Javier Prado 4200")
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
