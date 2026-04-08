package Payment.Integration.demo.dto;
import lombok.Data;
import java.util.List;

@Data
public class PayPalOrderRequest {
    private String intent;
    private List<PurchaseUnit> purchase_units;
}
