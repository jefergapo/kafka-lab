package com.jeff.eventbasedemo.domain.model;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public record WaiterRequest(UUID requestId, LocalDateTime requestTime, String tableNumber, Optional<String> assignedWaiterId) {
    public WaiterRequest(String tableNumber) {
        this(UUID.randomUUID(), LocalDateTime.now(), tableNumber, Optional.empty());
    }

    public WaiterRequest(String tableNumber, String assignedWaiterId) {
        this(UUID.randomUUID(), LocalDateTime.now(), tableNumber, Optional.of(assignedWaiterId));
    }
}