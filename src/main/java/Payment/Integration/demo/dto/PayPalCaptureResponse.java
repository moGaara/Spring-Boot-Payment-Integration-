package Payment.Integration.demo.dto;

import lombok.Data;

@Data
public class PayPalCaptureResponse {
    private String id;
    private String status;
}
