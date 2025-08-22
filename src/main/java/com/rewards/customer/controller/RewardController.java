package com.rewards.customer.controller;

import com.rewards.customer.model.Customer;
import com.rewards.customer.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/v1/customers")

public class RewardController {

    @Autowired
    RewardService rewardService;

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    @RequestMapping("/saveDetails")
    @PostMapping
    public ResponseEntity<String> saveReward(@Valid @RequestBody Customer customer){
    logger.info("Received request to save the details for the user: {}", customer.getFirstName());
    Customer customerDetails  = rewardService.saveReward(customer);
    String response;
    if (customerDetails != null){
        response = "Successfully saved the customer details";
    }
    else{
        response = "There was some issue. Please try again later!!";
    }
    logger.info("Successfully saved the details in our database for the user: {}", customer.getFirstName());
    return ResponseEntity.ok(response);

}

    @RequestMapping("/getDetails/{phoneNumber}")
    @GetMapping
    public ResponseEntity<String> getReward(@PathVariable Long phoneNumber) throws ExecutionException, InterruptedException {
        logger.info("Received request to get the details for the user of phone number: {}", phoneNumber);

        String response = rewardService.getReward(phoneNumber).get();
        logger.info("Successfully processed rewards for phone number: {}", phoneNumber);
        return ResponseEntity.ok(response);

    }

}
