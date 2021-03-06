package payment.provider.service.http.rest;

import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.Route;
import payment.provider.service.http.dto.request.PaymentRequest;
import payment.provider.service.http.dto.response.PaymentResponse;
import payment.provider.service.http.dto.response.PaymentResponseStatus;
import payment.provider.service.http.dto.response.error.ApiError;
import payment.provider.service.service.CreditCardService;

public final class PaymentApi extends AllDirectives {

    private final CreditCardService creditCardService;

    public PaymentApi(final CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    public Route routes() {
        return concat(processPayment());
    }

    private Route processPayment() {
        return get(() -> pathPrefix("payment", () -> entity(Jackson.unmarshaller(PaymentRequest.class), request ->
            creditCardService.processPayment(request.accountId, request.amount).fold(
                errorMessage -> complete(StatusCodes.NOT_FOUND, apiError(errorMessage), Jackson.marshaller()),
                success -> success.fold(
                    errorMessage -> complete(StatusCodes.BAD_REQUEST, failResponse(errorMessage, request), Jackson.marshaller()),
                    successMessage -> complete(StatusCodes.OK, successResponse(successMessage, request), Jackson.marshaller())
                )
            )
        )));
    }

    private ApiError apiError(final String message) {
        return new ApiError(message);
    }

    private PaymentResponse successResponse(final String message, final PaymentRequest request) {
        return new PaymentResponse(request.accountId, request.amount, PaymentResponseStatus.PAYMENT_SUCCESSFUL, message);
    }

    private PaymentResponse failResponse(final String message, final PaymentRequest request) {
        return new PaymentResponse(request.accountId, request.amount, PaymentResponseStatus.PAYMENT_FAILED, message);
    }
}
