package com.yanki.customers.interfaces.rest.controller;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerCatalogs;
import com.yanki.customers.domain.model.CustomerPage;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.domain.port.in.CreateCustomerUseCase;
import com.yanki.customers.domain.port.in.FindCustomerUseCase;
import com.yanki.customers.domain.port.in.ModifyCustomerUseCase;
import com.yanki.customers.domain.service.CustomerCommand;
import com.yanki.customers.domain.service.CustomerFilter;
import com.yanki.customers.domain.service.CustomerResult;
import com.yanki.customers.interfaces.rest.dto.CustomerResponseMapper;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Pruebas unitarias del controlador REST de clientes.
 * Valida la capa de presentacion usando mocks de los puertos de entrada hexagonales.
 */
@WebFluxTest(controllers = CustomerController.class)
@Import(CustomerResponseMapper.class)
@ImportAutoConfiguration(HttpMessageConvertersAutoConfiguration.class)
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockBean
    private FindCustomerUseCase findCustomerUseCase;

    @MockBean
    private ModifyCustomerUseCase modifyCustomerUseCase;

    @Mock
    private CustomerResponseMapper customerResponseMapper;

    @Test
    void shouldCreateCustomerAndReturn201() {
        Customer customer = personalCustomer();
        CustomerResult result = new CustomerResult(customer, "TmpPass12345");

        when(createCustomerUseCase.execute(any(CustomerCommand.class)))
                .thenReturn(Single.just(result));

        webTestClient.post().uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personalRequest())
                .exchange()
                .expectStatus().isCreated();
    }

    @Test
    void shouldGetCustomerByIdAndReturn200() {
        when(findCustomerUseCase.findById("C-1"))
                .thenReturn(Single.just(personalCustomer()));

        webTestClient.get().uri("/api/customers/C-1")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldGetCustomerByDocumentAndReturn200() {
        when(findCustomerUseCase.findByDocument(DocumentType.DNI, "70123456"))
                .thenReturn(Single.just(personalCustomer()));

        webTestClient.get().uri("/api/customers/document/DNI/70123456")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldListCustomersAndReturn200() {
        CustomerPage page = CustomerPage.of(List.of(personalCustomer()), 0, 20, 1);
        when(findCustomerUseCase.findAll(any(CustomerFilter.class)))
                .thenReturn(Single.just(page));

        webTestClient.get().uri("/api/customers?type=PERSONAL&profile=STANDARD&page=0&size=20")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldGetCatalogsAndReturn200() {
        when(findCustomerUseCase.getCatalogs())
                .thenReturn(Single.just(CustomerCatalogs.defaults()));

        webTestClient.get().uri("/api/customers/catalogs")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldUpdateCustomerAndReturn200() {
        when(modifyCustomerUseCase.executeUpdate(eq("C-1"), any(CustomerCommand.class)))
                .thenReturn(Single.just(personalCustomer()));

        webTestClient.put().uri("/api/customers/C-1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(personalRequest())
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldDeleteCustomerAndReturn204() {
        when(modifyCustomerUseCase.executeDelete("C-1"))
                .thenReturn(Completable.complete());

        webTestClient.delete().uri("/api/customers/C-1")
                .exchange()
                .expectStatus().isNoContent();
    }

    // =====================================================================
    // Factory methods
    // =====================================================================

    private com.yanki.customers.interfaces.rest.dto.CustomerRequest personalRequest() {
        return new com.yanki.customers.interfaces.rest.dto.CustomerRequest(
                CustomerType.PERSONAL, CustomerProfile.STANDARD, DocumentType.DNI, "70123456",
                null, "Juan", "Perez", "juan.perez@correo.com", "+51987654321",
                "Av. Javier Prado 4200", LocalDate.of(1990, 5, 20), null, null);
    }

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
                .phone("+51987654321")
                .address("Av. Javier Prado 4200")
                .birthDate(LocalDate.of(1990, 5, 20))
                .active(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusDays(1))
                .build();
    }
}
