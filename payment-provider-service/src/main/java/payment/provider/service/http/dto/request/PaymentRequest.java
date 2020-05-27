package payment.provider.service.http.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaymentRequest {

    public final Long accountId;
    public final double amount;

    @JsonCreator
    public PaymentRequest(
        @JsonProperty("accountId") final Long accountId,
        @JsonProperty("amount") final double amount
    ) {
        this.accountId = accountId;
        this.amount = amount;
    }
}
