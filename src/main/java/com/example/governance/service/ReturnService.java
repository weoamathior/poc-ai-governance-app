package com.example.governance.service;

import com.example.governance.model.Return;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReturnService {

    private static final List<Return> CANNED_RETURNS = List.of(
            new Return(1L, "ORD-100", "CUST-A", "Defective item", "PENDING"),
            new Return(2L, "ORD-101", "CUST-B", "Wrong size", "APPROVED"),
            new Return(3L, "ORD-102", "CUST-A", "Changed mind", "COMPLETED")
    );

    public Return getReturn(Long id) {
        return CANNED_RETURNS.stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public Return createReturn(Return returnRequest) {
        // Canned response — no persistence in this demo
        returnRequest.setId(99L);
        returnRequest.setStatus("PENDING");
        return returnRequest;
    }

    public List<Return> getAllReturns() {
        return CANNED_RETURNS;
    }
}
