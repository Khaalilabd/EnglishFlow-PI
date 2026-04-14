package com.englishflow.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymeeWebhookPayload {

    private String token;

    @JsonProperty("check_sum")
    private String checkSum;

    @JsonProperty("payment_status")
    private Boolean paymentStatus;

    @JsonProperty("order_id")
    private String orderId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    private String email;
    private String phone;
    private String note;
    private BigDecimal amount;

    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("received_amount")
    private BigDecimal receivedAmount;

    private BigDecimal cost;
}
