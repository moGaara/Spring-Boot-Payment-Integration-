package Payment.Integration.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentCreateResponse {
    private String paymentId;
    private String approvalUrl;
}
