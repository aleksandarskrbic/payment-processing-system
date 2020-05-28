package payment.processor.service.adapter.kafka.order.pipeline;

import java.util.concurrent.CompletionStage;
import payment.processor.service.adapter.http.payment.dto.response.PaymentResponse;
import payment.processor.service.adapter.http.user.dto.response.UserByEmailResponse;
import payment.processor.service.adapter.kafka.order.model.Order;

public class ElementWithContext {

    public static Builder Builder = new ElementWithContext.Builder().instance();

    public final Order order;
    public final CompletionStage<UserByEmailResponse> userByEmailResponse;
    public final CompletionStage<PaymentResponse> paymentResponse;

    private ElementWithContext(
        final Order order,
        final CompletionStage<UserByEmailResponse> userByEmailResponse,
        final CompletionStage<PaymentResponse> paymentResponse
    ) {
        this.order = order;
        this.userByEmailResponse = userByEmailResponse;
        this.paymentResponse = paymentResponse;
    }

    public static class Builder {

        private Order order;
        private CompletionStage<UserByEmailResponse> userByEmailResponse;
        private CompletionStage<PaymentResponse> paymentResponse;

        public Builder instance() {
            return new Builder();
        }

        public Builder withOrder(final Order order) {
            this.order = order;
            return this;
        }

        public Builder withUserByEmailResponse(final CompletionStage<UserByEmailResponse> userByEmailResponse) {
            this.userByEmailResponse = userByEmailResponse;
            return this;
        }

        public Builder withPaymentResponse(final CompletionStage<PaymentResponse> paymentResponse) {
            this.paymentResponse = paymentResponse;
            return this;
        }

        public ElementWithContext build() {
            return new ElementWithContext(order, userByEmailResponse, paymentResponse);
        }
    }
}
