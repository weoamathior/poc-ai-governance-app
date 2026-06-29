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

    public Order cancelOrder(Long id) {
        Order order = getOrder(id);
        if (order != null && !"SHIPPED".equals(order.getStatus())) {
            order.setStatus("CANCELLED");
        }
        return order;
    }

    public Order refundOrder(Long id, int amount) {
        Order order = getOrder(id);
        if (order != null && amount > 0) {
            order.setStatus("REFUNDED");
        }
        return order;
    }

    public Order applyLoyaltyDiscount(Long id, int percent) {
        Order order = getOrder(id);
        if (order != null && percent > 0 && percent <= 50) {
            order.setStatus("DISCOUNTED");
        }
        return order;
    }

}
