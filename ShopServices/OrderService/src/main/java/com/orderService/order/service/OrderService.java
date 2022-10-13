package com.orderService.order.service;

import com.orderService.order.dto.InventoryResponse;
import com.orderService.order.dto.OrderLineItemsDto;
import com.orderService.order.dto.OrderRequest;
import com.orderService.order.event.OrderPlacedEvent;
import com.orderService.order.model.Order;
import com.orderService.order.model.OrderLineItems;
import com.orderService.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.cloud.sleuth.Span;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final Tracer tracer;

    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public void placeOrder(OrderRequest orderRequest) {

        /* Map each dto to a real OrderLineItem */
        List<OrderLineItems> orderLineItemsList = orderRequest.getOrderLineItemsDtoList()
                .stream()
                .map(this::mapToDto)
                .toList();

        /* Create an Order entity */
        Order orderForPlacement = Order.builder()
                .orderNumber(UUID.randomUUID().toString())
                .orderLineItems(orderLineItemsList)
                .build();

        /* Extract all skucodes from orderLineItemsList to a list of strings */
        List<String> skuCodesList = orderLineItemsList
                .stream()
                .map(OrderLineItems::getSkuCode)
                .toList();

        Span inventoryServiceLookUp = tracer.nextSpan().name("InventoryServiceLookUp");

        try(Tracer.SpanInScope spanInScope = tracer.withSpan(inventoryServiceLookUp.start())){
            /* First , check the inventory service for items-availability ! */
            InventoryResponse[] inventoryResponsesArray = webClientBuilder.build().get()
                    .uri("http://inventory-service/api/v1/inventory",
                            uriBuilder -> uriBuilder.queryParam("skuCodeList", skuCodesList).build())
                    .retrieve()
                    .bodyToMono(InventoryResponse[].class)/* define the return data type */
                    .block(); /* make the request synchronous */

            /* traverse inventoryResponsesArray and check if all products are in stock */
            boolean allProductsInStock = Arrays.stream(inventoryResponsesArray)
                    .allMatch(InventoryResponse::isInStock);

            if (Boolean.TRUE.equals(allProductsInStock)) {
                orderRepository.save(orderForPlacement);
                kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(orderForPlacement.getOrderNumber()));
            }
            else
                throw new IllegalArgumentException("Product is not in stock, please try again later");
        }finally {
            inventoryServiceLookUp.end();
        }
    }

    private OrderLineItems mapToDto(OrderLineItemsDto orderLineItemsDto) {
        return OrderLineItems.builder()
                .price(orderLineItemsDto.getPrice())
                .quantity(orderLineItemsDto.getQuantity())
                .skuCode(orderLineItemsDto.getSkuCode())
                .build();
    }
}
