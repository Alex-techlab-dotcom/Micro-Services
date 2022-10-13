package com.orderService.order.controller;

import com.orderService.order.dto.OrderRequest;
import com.orderService.order.service.OrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping()
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallBackMethod")
    @TimeLimiter(name = "inventory")
    @Retry(name="inventory")
    public CompletableFuture<ResponseEntity<?>> placeOrder(@RequestBody OrderRequest orderRequest) {
        orderService.placeOrder(orderRequest);
        // return new ResponseEntity<>("Order was placed successfully!", HttpStatus.CREATED);
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>("Order was placed successfully!", HttpStatus.CREATED));
    }

    public CompletableFuture<ResponseEntity<?>> fallBackMethod(OrderRequest orderRequest, RuntimeException runtimeException) {
        return CompletableFuture
                .supplyAsync(() -> new ResponseEntity<>("Order-Inventory communication was not successful!", HttpStatus.EXPECTATION_FAILED));
        //new ResponseEntity<>("Order-Inventory communication was not successful!", HttpStatus.EXPECTATION_FAILED);
    }
}
