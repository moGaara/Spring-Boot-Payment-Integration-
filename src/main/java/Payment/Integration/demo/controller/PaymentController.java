package Payment.Integration.demo.controller;

import Payment.Integration.demo.dto.PaymentCaptureResponse;
import Payment.Integration.demo.dto.PaymentCreateResponse;
import Payment.Integration.demo.dto.PaymentRequest;
import Payment.Integration.demo.service.PaymentService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    @PostMapping("/{type}/create")
    public Mono<PaymentCreateResponse> create(
            @PathVariable String type,
            @RequestBody PaymentRequest request) {
        return service.create(type, request);
    }

    @PostMapping("/{type}/capture/{id}")
    public Mono<PaymentCaptureResponse> capture(
            @PathVariable String type,
            @PathVariable String id) {
        return service.capture(type, id);
    }
}