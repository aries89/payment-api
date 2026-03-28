package com.payment.dtos;

import com.payment.enums.PaymentMethod;
import com.payment.enums.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentRequest {

    @NotBlank(message = "Name is required")
    @Pattern(regexp = "^[A-Za-z -]+$", message = "Name can only contain letters, spaces, and hyphens")
    private String name;

    @NotBlank(message = "Last name is required")
    @Pattern(regexp = "^[A-Za-z -]+$", message = "Last name can only contain letters, spaces, and hyphens")
    private String lastName;

    @NotBlank(message = "Zip code is required")
    @Pattern(regexp = "^[0-9]{4}$", message = "Zip code can only have 4 digits")
    private String zipCode;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{16}$", message = "Card number can have only 16 digits")
    private String cardNumber;

    @NotNull(message = "Amount is required")
    private Double amount;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @NotNull(message = "Status is required")
    private PaymentStatus status;

    @NotNull(message = "Currency is required")
    private String currency;




}
