package Payment.Integration.demo.service;

import Payment.Integration.demo.dto.PaymentCaptureResponse;
import Payment.Integration.demo.dto.PaymentCreateResponse;
import Payment.Integration.demo.dto.PaymentRequest;
import Payment.Integration.demo.paymentStrategy.PaymentStrategyFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PaymentService {

    private final PaymentStrategyFactory factory;



    public PaymentCreateResponse create(String type, PaymentRequest request)
    {
        return factory.getStrategy(type).createPayment(request);
    }

    public PaymentCaptureResponse capture(String type, String paymentId)
    {
        return factory.getStrategy(type).capturePayment(paymentId);
    }
}
