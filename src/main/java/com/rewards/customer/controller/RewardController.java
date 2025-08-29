package com.rewards.customer.controller;

import com.rewards.customer.model.Customer;
import com.rewards.customer.model.CustomerResponse;
import com.rewards.customer.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;


@RestController
@RequestMapping("/api/v1/customers")

public class RewardController {

    private final RewardService rewardService;

    // Create a constructor that takes the dependency as an argument
    public RewardController(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    private static final Logger logger = LoggerFactory.getLogger(RewardController.class);

    /**
     * This method saves the Customer details to the datavbase
     *
     * @param customer
     * @return Success messages upon successfully inserted the data
     */
    @RequestMapping("/saveDetails")
    @PostMapping
    public ResponseEntity<String> saveReward(@Valid @RequestBody Customer customer) {
        logger.info("Received request to save the details for the user: {}", customer.getFirstName());
        Customer customerDetails = rewardService.saveReward(customer);
        String response;
        if (customerDetails != null) {
            response = "Successfully saved the customer details";
        } else {
            response = "There was some issue. Please try again later!!";
        }
        logger.info("Successfully saved the details in our database for the user: {}", customer.getFirstName());
        return ResponseEntity.ok(response);

    }

    /**
     * This method gets the rewards of the customer for the 3 months from the date to 3 months
     *
     * @param phoneNumber
     * @return returns the response with the rewards calculated
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping("/getDetails/{phoneNumber}")
    @GetMapping
    public ResponseEntity<CustomerResponse> getReward(@PathVariable Long phoneNumber) throws ExecutionException, InterruptedException {
        logger.info("Received request to get the details for the user of phone number: {}", phoneNumber);
        CustomerResponse response = new CustomerResponse();
        if (phoneNumber == null) {
            response.setMessage("Please provide the valid phone number to fectch teh details");
        }
        response = rewardService.getReward(phoneNumber).get();
        logger.info("Successfully processed rewards for phone number: {}", phoneNumber);
        return ResponseEntity.ok(response);

    }

}
