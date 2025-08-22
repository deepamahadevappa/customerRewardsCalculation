package com.rewards.customer.service;

import com.rewards.customer.model.Customer;
import java.util.concurrent.CompletableFuture;

public interface RewardService {

    public Customer saveReward(Customer customer);
    CompletableFuture<String> getReward(Long phoneNumber);

}
