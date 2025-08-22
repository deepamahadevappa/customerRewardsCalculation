package com.rewards.customer.serviceImpl;

import com.rewards.customer.exception.DatabaseFailureExcpetion;
import com.rewards.customer.exception.ResourceNotFoundException;
import com.rewards.customer.model.Customer;
import com.rewards.customer.repository.CustomerRepository;
import com.rewards.customer.service.RewardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class RewardServiceImpl implements RewardService {
    private static final Logger log = LoggerFactory.getLogger(RewardServiceImpl.class);

    @Autowired
    private MessageSource messageSource;

    @Autowired
    CustomerRepository customerRepository;

    @Override
    public Customer saveReward(Customer customer) {
        log.info("Attempting to save customer details for the User: {}", customer.getFirstName());
        if (customer.getShoppedDate() == null) {
            LocalDate todayDate = LocalDate.now();
            customer.setShoppedDate(todayDate);
        }
        Customer savedCustomer;
        try {
            savedCustomer = customerRepository.save(customer);
            log.info("Customer details are successfully saved for the user: {}", customer.getFirstName());

        } catch (DataAccessException e) {
            log.error("Failed to save new customer: {}", e.getMessage());
            throw new DatabaseFailureExcpetion("Failed to save customer data due to a database error" + e.getMessage());
        }
        return savedCustomer;
    }

    @Async
    @Override
    public CompletableFuture<String> getReward(Long phoneNumber) {
        log.info("Attempting to fetch customer details for User with phone number: {}", phoneNumber);
        String response = null;
        List<Customer> customerList;
        try{
            customerList =  customerRepository.findByPhoneNumber(phoneNumber);
            log.info("Successfully fetched the details of the User");
        }
        catch(DataAccessException e){
            log.error("Failed to find customer with Phonenumber {}: {}", phoneNumber, e.getMessage());
            throw new DatabaseFailureExcpetion("Could not retrieve customer due to a database error " + e.getMessage());
        }
        if (customerList == null ||  customerList.isEmpty()) {
            throw new ResourceNotFoundException(messageSource.getMessage("rewards.not_registered", null, Locale.getDefault()));
        }
            LocalDate today = LocalDate.now();
        List<Customer> eligibleCustomers = new ArrayList<>();
        LocalDate threeMonthsAgo = today.minusMonths(3);
                List<Customer> recentCustomers = customerList.stream().filter(rewardCustomer -> rewardCustomer.getShoppedDate().isAfter(threeMonthsAgo)).collect(Collectors.toList());
                        if (recentCustomers.isEmpty()) {
                            response =  messageSource.getMessage("rewards.not_active", null, Locale.getDefault());
                            return CompletableFuture.completedFuture(response);

                        } else {
                            eligibleCustomers = recentCustomers.stream().filter(customer -> customer.getPrice() >= 50 && customer.getShoppedDate().isAfter(threeMonthsAgo)).collect(Collectors.toList());
                        }
                        if (!recentCustomers.isEmpty() && eligibleCustomers.isEmpty()){
                            response =  messageSource.getMessage("rewards.not_eligible", null, Locale.getDefault());

                        } else {
                            int totalPoints = eligibleCustomers.stream()
                                    .mapToInt(customer -> {
                                        int price = customer.getPrice();
                                        if (price > 100) {
                                            return 50 + (price - 100) * 2;
                                        } else if (price > 50) {
                                            return price - 50;
                                        }
                                        return 0;
                                    })
                                    .sum();

                            int totalPrice = eligibleCustomers.stream()
                                    .mapToInt(Customer::getPrice)
                                    .sum();

                            String commaSeparatedPrices = eligibleCustomers.stream()
                                    .map(customer -> String.valueOf(customer.getPrice()))
                                    .collect(Collectors.joining(", "));
                            response = messageSource.getMessage("rewards.success", new Object[]{totalPoints, commaSeparatedPrices}, Locale.getDefault());
                        }
        return CompletableFuture.completedFuture(response);
    }
}