package com.example.governance.service;

import com.example.governance.model.Order;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class OrderService {

    public Order getOrder(Long id) {
        return null;
    }

    public Order createOrder(Order order) {
        return null;
    }

    public List<Order> getOrdersForCustomer(String customerId) {
        return Collections.emptyList();
    }
}
