package com.rewards.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseDetails {

    private Integer orderId;
    private String month;
    private Integer price;
    private Integer rewardsPoints;

    public PurchaseDetails(String string, @NotNull(message = "{price.notnull}") @Min(value = 10, message = "{price.min}") Integer price, @NotNull(message = "{orderId.notnull}") Integer orderId) {
    }
}
