package payment.provider.service.http.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaymentRequest {

    public final Long id;
    public final double amount;

    @JsonCreator
    public PaymentRequest(
        @JsonProperty("id") final Long id,
        @JsonProperty("amount") final double amount
    ) {
        this.id = id;
        this.amount = amount;
    }
}
