package com.jeff.eventbasedemo.infrastructure.controller;

import com.jeff.eventbasedemo.domain.model.WaiterRequest;
import com.jeff.eventbasedemo.domain.service.WaiterRequestProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/waiter")
public class WaiterRequestController {

    private final WaiterRequestProducer waiterRequestProducer;

    public WaiterRequestController(WaiterRequestProducer waiterRequestProducer) {
        this.waiterRequestProducer = waiterRequestProducer;
    }

    @PostMapping(value = "/request", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono<Void> requestWaiter(@RequestBody WaiterRequestPayload payload) {
        WaiterRequest request = new WaiterRequest(payload.tableNumber());
        return waiterRequestProducer.sendWaiterRequest(request);
    }

    public record WaiterRequestPayload(String tableNumber) {}
}
