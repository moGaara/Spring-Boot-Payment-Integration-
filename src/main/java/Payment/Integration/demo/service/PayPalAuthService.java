package Payment.Integration.demo.service;


import Payment.Integration.demo.dto.TokenResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Service

public class PayPalAuthService {

    private final WebClient webClient;
    public PayPalAuthService(WebClient webClient) {
        this.webClient = webClient;
    }
    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;



    public String getAccessToken()
    {
        String auth = clientId + ":" + clientSecret;
        String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());

        return webClient.post()
                .uri(baseUrl + "/v1/oauth2/token")
                .header("Authorization", "Basic " + encodedAuth)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .map(TokenResponse::getAccess_token)
                .block();
    }
}
