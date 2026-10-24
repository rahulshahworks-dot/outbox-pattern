package com.rahul.outboxorderservice.controller;

import com.rahul.outboxorderservice.entity.Order;
import com.rahul.outboxorderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> request) {
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        Order savedOrder = orderService.placeOrder(amount);
        return ResponseEntity.ok(savedOrder);
    }
}