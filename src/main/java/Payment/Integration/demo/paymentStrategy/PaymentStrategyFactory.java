package Payment.Integration.demo.paymentStrategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<String, PaymentStrategy> strategies;

    public PaymentStrategyFactory(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(String type) {
        PaymentStrategy strategy = strategies.get(type.toUpperCase());

        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported payment type");
        }

        return strategy;
    }
}