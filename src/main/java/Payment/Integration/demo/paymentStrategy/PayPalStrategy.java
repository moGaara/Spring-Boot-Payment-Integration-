package Payment.Integration.demo.paymentStrategy;

import Payment.Integration.demo.dto.*;
import Payment.Integration.demo.service.PayPalAuthService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component("PAYPAL")
public class PayPalStrategy implements PaymentStrategy {

    private final PayPalAuthService authService;
    private final WebClient webClient;

    public PayPalStrategy(PayPalAuthService authService, WebClient webClient) {
        this.authService = authService;
        this.webClient = webClient;
    }

    @Override
    public PaymentCreateResponse createPayment(PaymentRequest request) {

        String token = authService.getAccessToken();

        PayPalOrderRequest body = new PayPalOrderRequest();
        body.setIntent("CAPTURE");

        Amount amount = new Amount();
        amount.setCurrency_code(request.getCurrency());
        amount.setValue(request.getAmount());

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        body.setPurchase_units(List.of(unit));

        PayPalOrderResponse response = webClient.post()
                .uri("https://api-m.sandbox.paypal.com/v2/checkout/orders")
                .header("Authorization", "Bearer " + token)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(PayPalOrderResponse.class)
                .block();

        String approvalUrl = response.getLinks().stream()
                .filter(link -> "approve".equals(link.getRel()))
                .findFirst()
                .orElseThrow()
                .getHref();

        return PaymentCreateResponse.builder()
                .paymentId(response.getId())
                .approvalUrl(approvalUrl)
                .build();
    }

    @Override
    public PaymentCaptureResponse capturePayment(String orderId) {

        String token = authService.getAccessToken();

        PayPalCaptureResponse paypalResponse = webClient.post()
                .uri("https://api-m.sandbox.paypal.com/v2/checkout/orders/" + orderId + "/capture")
                .header("Authorization", "Bearer " + token)
                .header("Prefer", "return=representation") // Add this
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    System.err.println("PayPal error: " + errorBody); // Log the actual PayPal error message
                                    return Mono.error(new RuntimeException("PayPal capture failed: " + errorBody));
                                })
                )
                .bodyToMono(PayPalCaptureResponse.class)
                .block();

        return PaymentCaptureResponse.builder()
                .paymentId(paypalResponse.getId())
                .status(paypalResponse.getStatus())
                .build();
    }
}