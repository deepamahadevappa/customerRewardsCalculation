package com.rewards.customer.test.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewards.customer.controller.RewardController;
import com.rewards.customer.model.Customer;
import com.rewards.customer.service.RewardService;
import com.rewards.customer.exception.ResourceNotFoundException;
import com.rewards.customer.exception.DatabaseFailureExcpetion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(RewardController.class)
public class RewardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RewardService rewardService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void testSaveReward_Success() throws Exception {
        Customer mockCustomer = new Customer();
        mockCustomer.setFirstName("John");
        mockCustomer.setLastName("Doe");
        mockCustomer.setOrderId(1353);
        mockCustomer.setPrice(120);
        mockCustomer.setPhoneNumber(1234567890L);
        mockCustomer.setShoppedDate(LocalDate.now());
        when(rewardService.saveReward(any(Customer.class))).thenReturn(mockCustomer);

        mockMvc.perform(post("/api/v1/customers/saveDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCustomer)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Successfully saved the customer details"));
    }


    @Test
    void testSaveReward_ServiceThrowsDatabaseFailureException_ReturnsInternalServerError() throws Exception {

        Customer mockCustomer = new Customer();
        mockCustomer.setFirstName("John");
        mockCustomer.setLastName("Doe");
        mockCustomer.setOrderId(1353);
        mockCustomer.setPrice(120);
        mockCustomer.setPhoneNumber(1234567890L);
        mockCustomer.setShoppedDate(LocalDate.now());


        doThrow(new DatabaseFailureExcpetion("Failed to save customer data.")).when(rewardService).saveReward(any(Customer.class));


        mockMvc.perform(post("/api/v1/customers/saveDetails")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockCustomer)))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void testGetReward_Success() throws Exception {
        Long phoneNumber = 12345L;
        String expectedResponse = "Congratulations!!!!, you have received a total of 10 points for your order of $60";

        when(rewardService.getReward(phoneNumber))
                .thenReturn(completedFuture(expectedResponse));

        mockMvc.perform(get("/api/v1/customers/getDetails/{phoneNumber}", phoneNumber))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedResponse));
    }


    public void testGetReward_NotFoundFailure() throws Exception {
        Long phoneNumber = 67890L;
        String errorMessage = "Sorry this Phone Number is not Registerd. Please register!!";

       CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new ResourceNotFoundException(errorMessage));

        when(rewardService.getReward(phoneNumber))
                .thenReturn(future);

        mockMvc.perform(get("/api/v1/customers/getDetails/{phoneNumber}", phoneNumber))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\""+ errorMessage +"\"}"));
    }

    @Test
    public void testGetReward_DatabaseFailure() throws Exception {
        Long phoneNumber = 11223L;
        String errorMessage = "An unexpected error occurred during async processing.";

        CompletableFuture<String> future = new CompletableFuture<>();
        future.completeExceptionally(new DatabaseFailureExcpetion(errorMessage));

        when(rewardService.getReward(phoneNumber))
                .thenReturn(future);

        mockMvc.perform(get("/api/v1/customers/getDetails/{phoneNumber}", phoneNumber))
                .andExpect(status().isInternalServerError())
                .andExpect(content().json("{\"message\":\"" + errorMessage + "\"}"));
    }
}


