package com.jeff.eventbasedemo.domain.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;
@Service
public class WaiterAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(WaiterAssignmentService.class);

    // Simulación de una lista de meseros disponibles (en una aplicación real, esto vendría de una base de datos, etc.)
    private final List<String> availableWaiters = List.of("waiter1", "waiter2");
    private final Random random = new Random();

    public Optional<String> assignWaiter(String tableNumber) {
        if (availableWaiters.isEmpty()) {
            logger.warn("No waiters available to assign to table: {}", tableNumber);
            return Optional.empty();
        }

        // Lógica de asignación simple: selecciona un mesero aleatorio
        String assignedWaiter = availableWaiters.get(random.nextInt(availableWaiters.size()));
        logger.info("Assigned waiter {} to table: {}", assignedWaiter, tableNumber);
        return Optional.of(assignedWaiter);
    }

    // En una aplicación real, necesitarías métodos para rastrear la carga de trabajo de los meseros,
    // su estado (ocupado, disponible), etc.
}