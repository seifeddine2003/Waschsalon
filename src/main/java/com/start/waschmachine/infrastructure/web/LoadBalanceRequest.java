package com.start.waschmachine.infrastructure.web;

import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

public class LoadBalanceRequest {
    @DecimalMin("5")
    private BigDecimal amount;

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
