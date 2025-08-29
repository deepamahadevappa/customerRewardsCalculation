package com.rewards.customer.serviceImpl;

import com.rewards.customer.exception.DatabaseFailureExcpetion;
import com.rewards.customer.exception.ResourceNotFoundException;
import com.rewards.customer.model.Customer;
import com.rewards.customer.model.CustomerResponse;
import com.rewards.customer.model.ShoppedMonths;
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

    /**
     * This method saves the Customer details to the datavbase
     *
     * @param customer
     * @return Success messages upon successfully inserted the data
     */
    @Override
    public Customer saveReward(Customer customer) {
        log.info("Attempting to save customer details for the User: {}", customer.getFirstName());
        if (customer.getShoppedDate() == null) {
            LocalDate todayDate = LocalDate.now();
            customer.setShoppedDate(todayDate);
        }
        Customer savedCustomer;
        savedCustomer = customerRepository.save(customer);
        log.info("Customer details are successfully saved for the user: {}", customer.getFirstName());
        return savedCustomer;
    }

    /**
     * This methods has the rewards calculation logic with 3 months time frame,
     * if its beyond 3 months it gives the message to user to shop,
     * and if he shops for less then the minimum amount it tells to shops for the actual amount
     *
     * @param phoneNumber
     * @return returns the response with the month and the appropriate messages
     */
    @Async
    @Override
    public CompletableFuture<CustomerResponse> getReward(Long phoneNumber) {
        CustomerResponse customerResponse = new CustomerResponse();
        log.info("Attempting to fetch customer details for User with phone number: {}", phoneNumber);
        List<Customer> customerList;
        try {
            customerList = customerRepository.findByPhoneNumber(phoneNumber);
            log.info("Successfully fetched the details of the User");
        } catch (DataAccessException e) {
            log.error("Failed to find customer with Phonenumber {}: {}", phoneNumber, e.getMessage());
            throw new DatabaseFailureExcpetion("Could not retrieve customer due to a database error " + e.getMessage());
        }
        if (customerList == null || customerList.isEmpty()) {
            throw new ResourceNotFoundException(messageSource.getMessage("rewards.not_registered", null, Locale.getDefault()));
        }
        List<Customer> eligibleCustomers;
        LocalDate threeMonthsAgo = LocalDate.now().minusMonths(3);
        List<Customer> recentCustomers = customerList.stream().filter(rewardCustomer -> rewardCustomer.getShoppedDate().isAfter(threeMonthsAgo)).collect(Collectors.toList());
        if (recentCustomers.isEmpty()) {
            List<ShoppedMonths> shoppedMonthsList = createShoppedMonthsList(customerList, false);
            return CompletableFuture.completedFuture(
                    buildResponse(messageSource.getMessage("rewards.not_active", null, Locale.getDefault()), phoneNumber, shoppedMonthsList, 0)
            );

        } else {
            eligibleCustomers = recentCustomers.stream().filter(customer -> customer.getPrice() >= 50 && customer.getShoppedDate().isAfter(threeMonthsAgo)).collect(Collectors.toList());
        }
        customerResponse = rewardCalculation(phoneNumber, recentCustomers, eligibleCustomers, customerResponse, customerList);
        return CompletableFuture.completedFuture(customerResponse);
    }

    /**
     * This method calculates the reward points
     *
     * @param phoneNumber
     * @param recentCustomers
     * @param eligibleCustomers
     * @param customerResponse
     * @param customerList
     * @return returns the response based on the points
     */
    private CustomerResponse rewardCalculation(Long phoneNumber, List<Customer> recentCustomers, List<Customer> eligibleCustomers, CustomerResponse customerResponse, List<Customer> customerList) {
        List<ShoppedMonths> shoppedMonthsList = createShoppedMonthsList(recentCustomers, true);
        int totalPoints = shoppedMonthsList.stream()
                .mapToInt(ShoppedMonths::getRewardForTheMonth)
                .sum();
        String message;
        if (totalPoints == 0) {
            message = messageSource.getMessage("rewards.not_eligible", null, Locale.getDefault());
        } else {
            message = messageSource.getMessage("rewards.success", new Object[]{totalPoints}, Locale.getDefault());
        }
        return buildResponse(message, phoneNumber, shoppedMonthsList, totalPoints);
    }


    /**
     * Helper method to create a list of ShoppedMonths from a list of Customers.
     * It can either calculate points or set them to 0.
     */
    private List<ShoppedMonths> createShoppedMonthsList(List<Customer> customers, boolean calculatePoints) {
        return customers.stream()
                .map(customer -> {
                    int rewardsPoints = 0;
                    if (calculatePoints) {
                        int price = customer.getPrice();
                        if (price > 100) {
                            rewardsPoints = 50 + (price - 100) * 2;
                        } else if (price > 50) {
                            rewardsPoints = price - 50;
                        }
                    }
                    return new ShoppedMonths(
                            customer.getOrderId(),
                            customer.getShoppedDate().getMonth().toString(),
                            customer.getPrice(),
                            rewardsPoints
                    );
                })
                .collect(Collectors.toList());
    }

    private CustomerResponse buildResponse(String message, Long phoneNumber, List<ShoppedMonths> shoppedMonthsList, int totalPoints) {
        CustomerResponse customerResponse = new CustomerResponse();
        customerResponse.setPhoneNumber(phoneNumber);
        customerResponse.setShoppedMonthsList(shoppedMonthsList);
        customerResponse.setTotalRewards(totalPoints);
        customerResponse.setMessage(message);
        return customerResponse;
    }
}

