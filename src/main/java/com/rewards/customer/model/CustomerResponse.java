package com.rewards.customer.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponse {

    private Long phoneNumber;
    private List<PurchaseDetails> purchaseDetailsList;
    private Integer totalRewards;
    private String message;

}
