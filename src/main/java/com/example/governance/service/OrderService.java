package com.example.governance.service;

import com.example.governance.model.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // AI-ASSISTED: Generated with GitHub Copilot. Reviewed by [engineer].
    // Filters a customer's orders down to a single status.
    public List<Order> getOrdersByStatus(String customerId, String status) {
        List<Order> result = new ArrayList<>();
        for (Order o : getOrdersForCustomer(customerId)) {
            if (status.equals(o.getStatus())) {
                result.add(o);
            }
        }
        return result;
    }
}
