package Payment.Integration.demo.paymentStrategy;

import Payment.Integration.demo.dto.PaymentCaptureResponse;
import Payment.Integration.demo.dto.PaymentCreateResponse;
import Payment.Integration.demo.dto.PaymentRequest;

public interface PaymentStrategy {
    PaymentCreateResponse createPayment(PaymentRequest request);
    PaymentCaptureResponse capturePayment(String paymentId);
}
