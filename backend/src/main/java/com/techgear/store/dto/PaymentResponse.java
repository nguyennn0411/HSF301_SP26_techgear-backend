package com.techgear.store.dto;

public class PaymentResponse {
    private Long paymentId;
    private Long orderId;
    private String method;
    private String status;
    private String transactionCode;
    private Double amount;
    private String message;

    public PaymentResponse() {
    }

    public PaymentResponse(Long paymentId, Long orderId, String method, String status,
                           String transactionCode, Double amount, String message) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.method = method;
        this.status = status;
        this.transactionCode = transactionCode;
        this.amount = amount;
        this.message = message;
    }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTransactionCode() { return transactionCode; }
    public void setTransactionCode(String transactionCode) { this.transactionCode = transactionCode; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}