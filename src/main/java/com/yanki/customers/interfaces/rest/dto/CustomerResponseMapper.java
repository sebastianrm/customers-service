package com.yanki.customers.interfaces.rest.dto;

import com.yanki.customers.domain.model.Customer;
import org.springframework.stereotype.Component;

/**
 * Convierte las entidades de dominio en los DTOs publicados por la API.
 */
@Component
public class CustomerResponseMapper {

    /**
     * Convierte un cliente en su DTO de respuesta.
     *
     * @param customer cliente de dominio
     * @return DTO con los datos del cliente
     */
    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getCustomerId(), customer.getType(), customer.getProfile(),
                customer.getStatus(), customer.getDocumentType(), customer.getDocumentNumber(),
                customer.getBusinessName(), customer.getFirstName(), customer.getLastName(), customer.getEmail(),
                customer.getPhone(), customer.getAddress(), customer.getBirthDate(), customer.isActive(),
                customer.getCreatedAt(), customer.getUpdatedAt());
    }

    /**
     * Convierte un cliente en la respuesta de creacion con su contrasena temporal.
     *
     * @param customer cliente de dominio
     * @param temporaryPassword contrasena temporal generada
     * @return DTO de creacion
     */
    public CustomerCreatedResponse toCreatedResponse(Customer customer, String temporaryPassword) {
        return CustomerCreatedResponse.of(toResponse(customer), temporaryPassword);
    }
}
