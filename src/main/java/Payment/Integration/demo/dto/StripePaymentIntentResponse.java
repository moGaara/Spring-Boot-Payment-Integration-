package Payment.Integration.demo.dto;

public record StripePaymentIntentResponse(
        String id,
        String object,
        Long amount,
        String currency,
        String description,
        String status,
        String clientSecret,
        String paymentMethod,
        String confirmationMethod,
        Boolean captureMethod,
        Long created
) {
}
