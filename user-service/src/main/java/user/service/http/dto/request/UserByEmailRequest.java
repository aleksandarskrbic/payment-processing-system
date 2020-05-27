package user.service.http.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class UserByEmailRequest {

    public final String email;

    @JsonCreator
    public UserByEmailRequest(@JsonProperty("email") final String email) {
        this.email = email;
    }
}
