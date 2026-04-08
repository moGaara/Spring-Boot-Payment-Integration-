package Payment.Integration.demo.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String currency;
    private String amount;
    private String description;}
