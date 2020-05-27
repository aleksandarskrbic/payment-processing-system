package payment.provider.service.http.dto.response.error;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class ApiError {

    public final String message;

    @JsonCreator
    public ApiError(@JsonProperty("message") final String message) {
        this.message = message;
    }
}
