package com.example.governance.controller;

import com.example.governance.model.Order;
import com.example.governance.service.OrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

// AI-ASSISTED: Generated with GitHub Copilot. Reviewed by [engineer].
// Generates standard CRUD controller delegating to OrderService.
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable Long id) {
        return orderService.getOrder(id);
    }

    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @GetMapping("/customer/{customerId}")
    public List<Order> getOrdersForCustomer(@PathVariable String customerId) {
        return orderService.getOrdersForCustomer(customerId);
    }
}
