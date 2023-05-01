package com.example.orderservice.service;

import com.example.orderservice.dto.OrderLineItemDto;
import com.example.orderservice.dto.OrderRequest;
import com.example.orderservice.dto.OrderResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.model.OrderLineItem;
import com.example.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    public void placeOrder(OrderRequest orderRequest) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        order.setOrderLineItemList(orderRequest.getOrderLineItemDtoList()
                .stream()
                .map(this::mapToOrder)
                .toList()
        );

        orderRepository.save(order);
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
