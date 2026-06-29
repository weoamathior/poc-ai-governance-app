package com.example.governance.model;

/**
 * Simple return model. Plain getters and setters, no Lombok.
 */
public class Return {

    private Long id;
    private String orderId;
    private String customerId;
    private String reason;
    private String status;

    public Return() {
    }

    public Return(Long id, String orderId, String customerId, String reason, String status) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.reason = reason;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
