package Payment.Integration.demo;

import Payment.Integration.demo.dto.PaymentRequest;
import Payment.Integration.demo.paymentStrategy.StripeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class StripeStrategyTest {

    private StripeStrategy stripeStrategy;
    private PaymentRequest paymentRequest;

    @BeforeEach
    void setUp() {
        stripeStrategy = new StripeStrategy();
        ReflectionTestUtils.setField(stripeStrategy, "stripeSecretKey", "sk_test_testkey");
        
        paymentRequest = new PaymentRequest();
        paymentRequest.setAmount("10.00");
        paymentRequest.setCurrency("USD");
        paymentRequest.setDescription("Test payment");
    }

    @Test
    void createPayment_InvalidAmount_ThrowsException() {
        paymentRequest.setAmount("invalid");

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> stripeStrategy.createPayment(paymentRequest));
        
        assertTrue(exception.getMessage().contains("Invalid amount format"));
    }

    @Test
    void convertToCents_ValidAmount_ReturnsCorrectValue() {
        Long result = (Long) ReflectionTestUtils.invokeMethod(stripeStrategy, "convertToCents", "10.00");
        assertEquals(1000L, result);
    }

    @Test
    void convertToCents_InvalidAmount_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> 
                ReflectionTestUtils.invokeMethod(stripeStrategy, "convertToCents", "invalid"));
    }

    @Test
    void convertToCents_ZeroAmount_ReturnsZero() {
        Long result = (Long) ReflectionTestUtils.invokeMethod(stripeStrategy, "convertToCents", "0.00");
        assertEquals(0L, result);
    }

    @Test
    void convertToCents_FractionalAmount_ReturnsCorrectValue() {
        Long result = (Long) ReflectionTestUtils.invokeMethod(stripeStrategy, "convertToCents", "10.99");
        assertEquals(1099L, result);
    }
}
