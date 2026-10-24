package com.rahul.outboxorderservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rahul.outboxorderservice.entity.Order;
import com.rahul.outboxorderservice.entity.OutboxEvent;
import com.rahul.outboxorderservice.repository.OrderRepository;
import com.rahul.outboxorderservice.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public Order placeOrder(BigDecimal amount) {
        // 1. Business entity save (ID auto-generate hogi)
        Order order = Order.builder()
                .amount(amount)
                .status("CREATED")
                .build();
        Order savedOrder = orderRepository.save(order);

        // 2. Outbox event save
        try {
            String payloadJson = objectMapper.writeValueAsString(savedOrder);

            OutboxEvent event = OutboxEvent.builder()
                    .aggregateId(savedOrder.getId())
                    .payload(payloadJson)
                    .status("PENDING")
                    .build();

            outboxRepository.save(event);
        } catch (Exception e) {
            throw new RuntimeException("Outbox payload serialization failed", e);
        }

        return savedOrder;
    }
}