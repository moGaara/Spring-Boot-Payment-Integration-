package Payment.Integration.demo.dto;
import lombok.Data;
import java.util.List;

@Data
public class PayPalOrderResponse {
    private String id;
    private String status;
    private List<Link> links;
}
