package Payment.Integration.demo.paymentStrategy;

import Payment.Integration.demo.dto.PaymentCaptureResponse;
import Payment.Integration.demo.dto.PaymentCreateResponse;
import Payment.Integration.demo.dto.PaymentRequest;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.exception.StripeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;

@Slf4j
@Component("STRIPE")
public class StripeStrategy implements PaymentStrategy {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentCreateResponse createPayment(PaymentRequest request) {
        try {
            long amountInCents = convertToCents(request.getAmount());
            
            PaymentIntentCreateParams.AutomaticPaymentMethods automaticPaymentMethods =
                    PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                            .setEnabled(true)
                            .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                            .build();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(request.getCurrency().toLowerCase())
                    .setDescription(request.getDescription())
                    .setCaptureMethod(PaymentIntentCreateParams.CaptureMethod.MANUAL)
                    .setAutomaticPaymentMethods(automaticPaymentMethods)
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);
            
            log.info("Created Stripe PaymentIntent: {}", paymentIntent.getId());

            return PaymentCreateResponse.builder()
                    .paymentId(paymentIntent.getId())
                    .approvalUrl(paymentIntent.getClientSecret())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe payment creation failed: {}", e.getMessage());
            throw new RuntimeException("Failed to create Stripe payment: " + e.getMessage(), e);
        }
    }

    @Override
    public PaymentCaptureResponse capturePayment(String paymentIntentId) {
        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            
            PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                    .setPaymentMethod("pm_card_visa")
                    .build();
            
            PaymentIntent confirmedIntent = paymentIntent.confirm(confirmParams);
            
            log.info("Confirmed Stripe PaymentIntent: {} with status: {}", 
                    confirmedIntent.getId(), confirmedIntent.getStatus());

            return PaymentCaptureResponse.builder()
                    .paymentId(confirmedIntent.getId())
                    .status(confirmedIntent.getStatus())
                    .build();

        } catch (StripeException e) {
            log.error("Stripe payment capture failed for payment {}: {}", paymentIntentId, e.getMessage());
            throw new RuntimeException("Failed to capture Stripe payment: " + e.getMessage(), e);
        }
    }

    private long convertToCents(String amount) {
        try {
            BigDecimal decimal = new BigDecimal(amount);
            return decimal.multiply(new BigDecimal(100)).longValue();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amount, e);
        }
    }
}
