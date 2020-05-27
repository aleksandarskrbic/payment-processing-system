package user.service.http.rest;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import user.service.http.dto.request.UserByEmailRequest;
import user.service.http.dto.response.UserByEmailResponse;
import user.service.http.dto.response.error.ApiError;
import user.service.model.User;
import user.service.service.UserService;

public final class UserApi extends AllDirectives {

    private final UserService userService;

    public UserApi(final UserService userService) {
        this.userService = userService;
    }

    public Route routes() {
        return concat(findUserByEmail());
    }

    private Route findUserByEmail() {
        return get(() -> pathPrefix("user", () -> entity(Jackson.unmarshaller(UserByEmailRequest.class), request ->
            userService.findByEmail(request.email).fold(
                errorMessage -> complete(StatusCodes.NOT_FOUND, apiError(errorMessage), Jackson.marshaller()),
                user -> complete(StatusCodes.OK, successfulResponse(user), Jackson.marshaller())
            )
        )));
    }

    private ApiError apiError(final String message) {
        return new ApiError(message);
    }

    private UserByEmailResponse successfulResponse(final User user) {
        return new UserByEmailResponse(user.id, user);
    }
}
