package com.example.governance.model;

/**
 * Simple order model. Plain getters and setters, no Lombok.
 */
public class Order {

    private Long id;
    private String customerId;
    private String productCode;
    private Integer quantity;
    private String status;

    public Order() {
    }

    public Order(Long id, String customerId, String productCode, Integer quantity, String status) {
        this.id = id;
        this.customerId = customerId;
        this.productCode = productCode;
        this.quantity = quantity;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
