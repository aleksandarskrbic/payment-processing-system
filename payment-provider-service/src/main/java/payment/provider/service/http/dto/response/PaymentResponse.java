package payment.provider.service.http.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class PaymentResponse {

    public final PaymentResponseStatus status;
    public final String message;

    @JsonCreator
    public PaymentResponse(
        @JsonProperty("status") final PaymentResponseStatus status,
        @JsonProperty("message") final String message
    ) {
        this.status = status;
        this.message = message;
    }
}
