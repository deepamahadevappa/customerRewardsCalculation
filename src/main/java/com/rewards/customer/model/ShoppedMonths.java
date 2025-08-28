package com.rewards.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppedMonths {


    private String ShoppedMonths;
    private Integer price;
    private Integer orderId;
}
