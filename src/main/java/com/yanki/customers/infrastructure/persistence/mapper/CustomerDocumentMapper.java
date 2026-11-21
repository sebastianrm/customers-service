package com.yanki.customers.infrastructure.persistence.mapper;

import com.yanki.customers.domain.model.Customer;
import com.yanki.customers.domain.model.CustomerProfile;
import com.yanki.customers.domain.model.CustomerStatus;
import com.yanki.customers.domain.model.CustomerType;
import com.yanki.customers.domain.model.DocumentType;
import com.yanki.customers.infrastructure.persistence.document.CustomerDocument;

/**
 * Traductor estatico entre el modelo de dominio y el documento MongoDB.
 */
public final class CustomerDocumentMapper {

    /**
     * Constructor privado para evitar la instanciacion.
     */
    private CustomerDocumentMapper() {
    }

    /**
     * Convierte un documento MongoDB en una entidad de dominio.
     *
     * @param document documento de MongoDB
     * @return entidad de dominio
     */
    public static Customer toDomain(CustomerDocument document) {
        if (document == null) {
            return null;
        }
        return Customer.builder()
                .customerId(document.getCustomerId())
                .type(CustomerType.valueOf(document.getType()))
                .profile(CustomerProfile.valueOf(document.getProfile()))
                .status(CustomerStatus.valueOf(document.getStatus()))
                .documentType(DocumentType.valueOf(document.getDocumentType()))
                .documentNumber(document.getDocumentNumber())
                .businessName(document.getBusinessName())
                .firstName(document.getFirstName())
                .lastName(document.getLastName())
                .email(document.getEmail())
                .phone(document.getPhone())
                .address(document.getAddress())
                .birthDate(document.getBirthDate())
                .active(document.isActive())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    /**
     * Convierte una entidad de dominio en un documento MongoDB.
     *
     * @param customer entidad de dominio
     * @return documento de MongoDB
     */
    public static CustomerDocument toDocument(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerDocument.builder()
                .customerId(customer.getCustomerId())
                .type(customer.getType().name())
                .profile(customer.getProfile().name())
                .status(customer.getStatus().name())
                .documentType(customer.getDocumentType().name())
                .documentNumber(customer.getDocumentNumber())
                .businessName(customer.getBusinessName())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .birthDate(customer.getBirthDate())
                .active(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
