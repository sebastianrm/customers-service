
package com.yanki.customers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the customers-service microservice.
 *
 * <p>Part of the Yanki banking platform. The service follows the
 * database-per-service pattern: it owns and accesses only its own MongoDB
 * database and never reads nor writes the data owned by another
 * microservice. Cross-service collaboration is performed through Kafka
 * events and never through direct REST calls.</p>
 *
 * <p>This class is responsible for bootstrapping the application using Spring Boot.</p>
 *
 * <p><strong>Author:</strong> Sebastian Retamal</p>
 * <p><strong>Email:</strong> semorale</p>
 */
@SpringBootApplication
public class CustomersServiceApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments forwarded to the Spring context
     */
    public static void main(String[] args) {
        SpringApplication.run(CustomersServiceApplication.class, args);
    }
}
