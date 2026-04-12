package Payment.Integration.demo.paymentStrategy;

import Payment.Integration.demo.dto.*;
import Payment.Integration.demo.exception.PaymentException;
import Payment.Integration.demo.service.PayPalAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component("PAYPAL")
@Slf4j
public class PayPalStrategy implements PaymentStrategy {


//    private static final String BASE_URL = "https://api-m.sandbox.paypal.com";

    private final PayPalAuthService authService;
    private final WebClient webClient;

    public PayPalStrategy(PayPalAuthService authService, WebClient webClient) {
        this.authService = authService;
        this.webClient = webClient;
    }

    @Override
    public Mono<PaymentCreateResponse> createPayment(PaymentRequest request) {
        String token = authService.getAccessToken();

        PayPalOrderRequest body = buildOrderRequest(request);

        String BASE_URL = authService.getBaseUrl();

        return webClient.post()
                .uri(BASE_URL + "/v2/checkout/orders")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        this::handleError
                )
                .bodyToMono(PayPalOrderResponse.class)
                .map(response -> {
                    String approvalUrl = response.getLinks().stream()
                            .filter(link -> "approve".equals(link.getRel()))
                            .findFirst()
                            .orElseThrow(() -> new PaymentException("No approval URL returned", 502))
                            .getHref();

                    return PaymentCreateResponse.builder()
                            .paymentId(response.getId())
                            .approvalUrl(approvalUrl)
                            .build();
                });
    }

    @Override
    public Mono<PaymentCaptureResponse> capturePayment(String orderId) {
        String token = authService.getAccessToken();
        String BASE_URL = authService.getBaseUrl();

        return webClient.post()
                .uri(BASE_URL + "/v2/checkout/orders/" + orderId + "/capture")
                .header("Authorization", "Bearer " + token)
                .header("Prefer", "return=representation")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        this::handleError
                )
                .bodyToMono(PayPalCaptureResponse.class)
                .map(response -> PaymentCaptureResponse.builder()
                        .paymentId(response.getId())
                        .status(response.getStatus())
                        .build());
    }

    // ---- helpers ----

    private PayPalOrderRequest buildOrderRequest(PaymentRequest request) {
        Amount amount = new Amount();
        amount.setCurrency_code(request.getCurrency());
        amount.setValue(request.getAmount());

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        PayPalOrderRequest body = new PayPalOrderRequest();
        body.setIntent("CAPTURE");
        body.setPurchase_units(List.of(unit));

        return body;
    }

    private Mono<Throwable> handleError(ClientResponse response) {
        return response.bodyToMono(String.class)
                .doOnNext(error -> log.error("PayPal error [{}]: {}", response.statusCode().value(), error))
                .map(error -> new PaymentException("PayPal error: " + error, response.statusCode().value()));
    }
}