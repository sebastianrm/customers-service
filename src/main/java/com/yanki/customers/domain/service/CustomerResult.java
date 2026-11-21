package com.yanki.customers.domain.service;

import com.yanki.customers.domain.model.Customer;

/**
 * Resultado de la creacion de un cliente.
 *
 * <p>La contrasena temporal solo viaja en este resultado: nunca se expone
 * en listados ni en consultas posteriores.</p>
 *
 * @param customer cliente creado
 * @param temporaryPassword contrasena temporal generada para el cliente
 */
public record CustomerResult(Customer customer, String temporaryPassword) {
}
