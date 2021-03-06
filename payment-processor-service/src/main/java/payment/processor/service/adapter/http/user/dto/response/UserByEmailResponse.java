package payment.processor.service.adapter.http.user.dto.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import payment.processor.service.adapter.http.user.dto.User;

public final class UserByEmailResponse {

    public final Long accountId;
    public final User user;

    @JsonCreator
    public UserByEmailResponse(
        @JsonProperty("accountId") final Long accountId,
        @JsonProperty("userDetails") final User user
    ) {
        this.accountId = accountId;
        this.user = user;
    }
}
