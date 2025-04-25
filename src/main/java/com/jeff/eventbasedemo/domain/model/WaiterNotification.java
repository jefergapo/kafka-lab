package com.jeff.eventbasedemo.domain.model;

import java.util.UUID;

public record WaiterNotification(UUID requestId, String tableNumber) {
}