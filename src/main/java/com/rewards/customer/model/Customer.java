package com.rewards.customer.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "{firstName.notnull}")
    private String firstName;
    @NotNull(message = "{lastName.notnull}")
    private String lastName;
    @NotNull(message = "{orderId.notnull}")
    private Integer orderId;
    @NotNull(message = "{price.notnull}")
    @Min(value = 10, message = "{price.min}")
    private Integer price;
    @NotNull(message = "{phoneNumber.notnull}")
    @Min(value = 1_000_000_000L, message = "{phoneNumber.invalid}")
    @Max(value = 9_999_999_999L, message = "{phoneNumber.invalid}")
    private Long phoneNumber;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @PastOrPresent(message = "{shoppedDate.noFutureDate}")
    private LocalDate shoppedDate;


}
