package payment.processor.service.adapter.http.user.dto.response.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApiError {

    public final String message;

    @JsonCreator
    public ApiError(@JsonProperty("message") final String message) {
        this.message = message;
    }
}
