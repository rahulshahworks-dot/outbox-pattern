package com.rahul.outboxorderservice.repository;

import com.rahul.outboxorderservice.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {}
