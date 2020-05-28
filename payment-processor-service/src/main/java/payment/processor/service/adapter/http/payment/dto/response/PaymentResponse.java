package payment.processor.service.adapter.http.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaymentResponse {

    public final Long accountId;
    public final double amount;
    public final PaymentResponseStatus status;
    public final String message;

    @JsonCreator
    public PaymentResponse(
        @JsonProperty("accountId") final Long accountId,
        @JsonProperty("amount") final double amount,
        @JsonProperty("status") final PaymentResponseStatus status,
        @JsonProperty("message") final String message
    ) {
        this.accountId = accountId;
        this.amount = amount;
        this.status = status;
        this.message = message;
    }
}
