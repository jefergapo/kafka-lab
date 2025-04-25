package com.jeff.eventbasedemo.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public record WaiterRequest(UUID requestId, LocalDateTime requestTime, String tableNumber) {
    public WaiterRequest(String tableNumber) {
        this(UUID.randomUUID(), LocalDateTime.now(), tableNumber);
    }
}