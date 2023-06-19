package com.example.orderservice.service;

import com.example.orderservice.dto.InventoryResponseDto;
import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.event.OrderPlacedEvent;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
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
    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public String placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderLineItemList(orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToOrder)
                .toList()
        );

        List<String> skuCodes = order.getOrderLineItemList()
                .stream()
                .map(OrderLineItem::getSkuCode)
                .toList();

        // Check in inventory if the product is in stock.
        InventoryResponseDto[] inventoryRespArr = webClientBuilder.build()
                .get()
                .uri("http://inventory-service/api/inventory",
                        uriBuilder -> uriBuilder.queryParam("skuCode", skuCodes).build()
                )
                .retrieve()
                .bodyToMono(InventoryResponseDto[].class)
                .block();

        assert inventoryRespArr != null;
        boolean allProductsInStock = Arrays.stream(inventoryRespArr).allMatch(InventoryResponseDto::isInStock);

        if(allProductsInStock) {
            orderRepository.save(order);
            kafkaTemplate.send("order-placed", new OrderPlacedEvent(order.getOrderNumber()));
            return "Order placed successfully!";
        } else {
            throw new IllegalArgumentException("Some products are not in stock!");
        }
    }

    private OrderLineItem mapToOrder(OrderLineItemDto orderLineItemDto) {
        return OrderLineItem.builder()
                .price(orderLineItemDto.getPrice())
                .quantity(orderLineItemDto.getQuantity())
                .skuCode(orderLineItemDto.getSkuCode())
                .build();
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToOrderResponse)
                .toList();
    }

    private OrderResponse mapToOrderResponse(Order order) {
        return OrderResponse.builder()
                .orderNumber(order.getOrderNumber())
                .orderLineItemDtoList(
                        order.getOrderLineItemList().stream()
                                .map(this::mapToOrderItemDto)
                                .toList()
                )
                .build();
    }

    private OrderLineItemDto mapToOrderItemDto(OrderLineItem orderLineItem) {
        return OrderLineItemDto.builder()
                .skuCode(orderLineItem.getSkuCode())
                .price(orderLineItem.getPrice())
                .quantity(orderLineItem.getQuantity())
                .build();
    }
}
