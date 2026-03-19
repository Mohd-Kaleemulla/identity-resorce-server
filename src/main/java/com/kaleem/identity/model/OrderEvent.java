package com.kaleem.identity.model;

public class OrderEvent {

    private String orderId;
    private String product;
    private int quantity;
    private String status;

    public OrderEvent() {}

    public OrderEvent(String orderId, String product, int quantity, String status) {
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }

    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "OrderEvent{orderId='" + orderId + "', product='" + product +
                "', quantity=" + quantity + ", status='" + status + "'}";
    }
}