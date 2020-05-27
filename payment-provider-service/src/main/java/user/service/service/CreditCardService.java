package user.service.service;

import io.vavr.control.Either;
import user.service.model.CreditCard;
import user.service.persistence.CreditCardRepository;

public final class CreditCardService {

    private final CreditCardRepository repository;

    public CreditCardService(final CreditCardRepository repository) {
        this.repository = repository;
    }

    /**
     *  Try to process payment for a given account id.
     *
     * @param accountId of account id.
     * @param amount to be withdrew.
     * @return Either of failed message because credit card is not found,
     * or either message of failure if card is found but withdraw has failed,
     * or successful message upon successful withdraw.
     */
    public Either<String, Either<String, String>> processPayment(final Long accountId, final Double amount) {
        return repository.findByAccountId(accountId)
            .map(cc -> cc.withdraw(amount))
            .map(this::withdraw)
            .toEither("Credit Card for account id = " + accountId + " not found.");
    }

    /**
     *  Try to withdraw the amount from credit card.
     *
     * @param creditCardEither error message or credit card with new balance after successful withdraw.
     * @return message weather new state of credit card is saved.
     */
    private Either<String, String> withdraw(final Either<String , CreditCard> creditCardEither) {
        return creditCardEither.fold(Either::left, creditCard -> {
            repository.save(creditCard);
            return Either.right("Withdraw successful.");
        });
    }
}
