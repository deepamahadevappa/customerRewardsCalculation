package com.rewards.customer.test.service;


import com.rewards.customer.exception.DatabaseFailureExcpetion;
import com.rewards.customer.exception.ResourceNotFoundException;
import com.rewards.customer.model.Customer;
import com.rewards.customer.model.CustomerResponse;
import com.rewards.customer.model.ShoppedMonths;
import com.rewards.customer.repository.CustomerRepository;
import com.rewards.customer.serviceImpl.RewardServiceImpl;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataAccessException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // This line fixes the UnnecessaryStubbingException
class RewardServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private RewardServiceImpl rewardService;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        Customer testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setFirstName("John");
        testCustomer.setLastName("Doe");
        testCustomer.setShoppedDate(LocalDate.now());
        testCustomer.setPhoneNumber(123456789L);
        testCustomer.setPrice(75);

        when(messageSource.getMessage("rewards.not_registered", null, Locale.getDefault()))
                .thenReturn("Sorry this Phone Number is not registered. Please register!!");
        when(messageSource.getMessage("rewards.not_active", null, Locale.getDefault()))
                .thenReturn("Rewards are calculated for the last 3 months!! Shop now and earn rewards.");
        when(messageSource.getMessage("rewards.not_eligible", null, Locale.getDefault()))
                .thenReturn("Sorry no rewards points!! Please shop for minimum of $50");
        when(messageSource.getMessage(eq("rewards.success"), any(Object[].class), any(Locale.class)))
                .thenAnswer(invocation -> {
                    Object[] args = invocation.getArgument(1);
                    return String.format("Congratulations!!!!, you have received a total of %s points for your order", args[0]);
                });
    }

    // --- Test cases for saveReward() ---

    @Test
    void testSaveReward_Success() {
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        assertDoesNotThrow(() -> rewardService.saveReward(getCustomers().get(0)));
        verify(customerRepository, times(1)).save(getCustomers().get(0));
    }


    // --- Test cases for getReward() ---

    @Test
    void testGetReward_NotFound() {
        when(customerRepository.findByPhoneNumber(anyLong())).thenReturn(Collections.emptyList());
        assertThrows(ResourceNotFoundException.class, () -> rewardService.getReward(123L).get());
    }

    @Test
    void testGetReward_DatabaseFailure() {
        when(customerRepository.findByPhoneNumber(anyLong())).thenThrow(new DataAccessException("Database error") {});
        assertThrows(DatabaseFailureExcpetion.class, () -> rewardService.getReward(123L).get());
    }

    @Test
    void testGetReward_NotActive() throws ExecutionException, InterruptedException {
        List<Customer> oldCustomers =getCustomers();
        oldCustomers.get(0).setShoppedDate(LocalDate.now().minusMonths(3));
        oldCustomers.get(0).setPrice(500);
        when(customerRepository.findByPhoneNumber(anyLong())).thenReturn(oldCustomers);
        String expectedMessage = "Rewards are calculated for the last 3 months!! Shop now and earn rewards.";
        CustomerResponse result = response(expectedMessage, oldCustomers.get(0).getPrice());
        result = rewardService.getReward(123L).get();
        assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    void testGetReward_NotEligible() throws ExecutionException, InterruptedException {
        List<Customer> recentIneligibleCustomers = getCustomers();
        recentIneligibleCustomers.get(0).setShoppedDate(LocalDate.now());
        recentIneligibleCustomers.get(0).setPrice(7);
        when(customerRepository.findByPhoneNumber(anyLong())).thenReturn(recentIneligibleCustomers);
        String expectedMessage = "Sorry no rewards points!! Please shop for minimum of $50";
        CustomerResponse result = response(expectedMessage, recentIneligibleCustomers.get(0).getPrice());
        result = rewardService.getReward(123L).get();
        assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    void testGetReward_Success_51to100Price() throws ExecutionException, InterruptedException {
        List<Customer> customers = getCustomers();
        customers.get(0).setShoppedDate(LocalDate.now());
        customers.get(0).setPrice(75);
        when(customerRepository.findByPhoneNumber(anyLong())).thenReturn(customers);
        String expectedMessage = "Congratulations!!!!, you have received a total of 25 points for your order";
        CustomerResponse result = response(expectedMessage, customers.get(0).getPrice());
        result = rewardService.getReward(123L).get();
        assertEquals(expectedMessage, result.getMessage());
    }

    @Test
    void testGetReward_Success_Over100Price() throws ExecutionException, InterruptedException {
        List<Customer> customers = getCustomers();
        customers.get(0).setShoppedDate(LocalDate.now());
        customers.get(0).setShoppedDate(LocalDate.now());
        customers.get(0).setPrice(120);
        when(customerRepository.findByPhoneNumber(anyLong())).thenReturn(customers);
        String expectedMessage = "Congratulations!!!!, you have received a total of 90 points for your order";
        CustomerResponse result = response(expectedMessage, customers.get(0).getPrice());
        result = rewardService.getReward(123L).get();
        assertEquals(expectedMessage, result.getMessage());
    }
    private List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        Customer customer = new Customer();
        customer.setId(123L);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setOrderId(124);
        customer.setPrice(75);
        customer.setPhoneNumber(123456789L);
        customer.setShoppedDate(LocalDate.now());
        customers.add(customer);
        return customers;
    }

    private CustomerResponse response(String message, Integer price) {
        CustomerResponse response = new CustomerResponse();
        response.setMessage(message);
        response.setPhoneNumber(123L);
        response.setShoppedMonthsList(getShoppedMonths(price));
        return response;

    }

    private List<ShoppedMonths> getShoppedMonths(Integer price) {
        List<ShoppedMonths> shoppedMonths = new ArrayList<>();
        ShoppedMonths shoppedMonth1 = new ShoppedMonths();
        shoppedMonth1.setMonth("August");
        shoppedMonth1.setPrice(price);
        shoppedMonth1.setOrderId(123);
        shoppedMonths.add(shoppedMonth1);
        return shoppedMonths;
    }

}