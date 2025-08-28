package com.rewards.customer.service;

import com.rewards.customer.model.Customer;
import com.rewards.customer.model.CustomerResponse;

import java.util.concurrent.CompletableFuture;

public interface RewardService {

    Customer saveReward(Customer customer);

    CompletableFuture<CustomerResponse> getReward(Long phoneNumber);

}
