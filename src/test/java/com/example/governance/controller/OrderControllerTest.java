package com.example.governance.controller;

import com.example.governance.model.Order;
import com.example.governance.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    void getOrderReturnsOk() throws Exception {
        when(orderService.getOrder(eq(1L)))
                .thenReturn(new Order(1L, "CUST-1", "PROD-001", 2, "NEW"));

        mockMvc.perform(get("/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createOrderReturnsOk() throws Exception {
        when(orderService.createOrder(any(Order.class)))
                .thenReturn(new Order(1L, "CUST-1", "PROD-001", 2, "NEW"));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":\"CUST-1\",\"productCode\":\"PROD-001\",\"quantity\":2}"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrdersForCustomerReturnsOk() throws Exception {
        when(orderService.getOrdersForCustomer(eq("CUST-1")))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/customer/CUST-1"))
                .andExpect(status().isOk());
    }
}
