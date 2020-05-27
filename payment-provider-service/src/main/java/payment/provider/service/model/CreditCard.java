package payment.provider.service.model;

import io.vavr.control.Either;

public final class CreditCard {

    public final Long id;
    public final Long accountId;
    public final String cardNumber;
    public final Double balance;

    public CreditCard(
        final Long id,
        final Long accountId,
        final String cardNumber,
        final Double balance
    ) {
        this.id = id;
        this.accountId = accountId;
        this.cardNumber = cardNumber;
        this.balance = balance;
    }

    public Either<String, CreditCard> withdraw(final Double amount) {
        final double newBalance = balance - amount;

        if (newBalance >= 0) {
            return Either.right(withBalance(newBalance));
        } else {
            return Either.left("Insufficient funds for credit card with id " + id);
        }
    }

    private CreditCard withBalance(final Double balance) {
        return new CreditCard(id, accountId, cardNumber, balance);
    }
}
