package Payment.Integration.demo.dto;

public record StripePaymentIntentRequest(
        Long amount,
        String currency,
        String description,
        String paymentMethod,
        String confirmationMethod,
        Boolean captureMethod
) {
}
