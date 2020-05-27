package payment.provider.service.service;

import io.vavr.control.Either;
import payment.provider.service.model.CreditCard;
import payment.provider.service.persistence.CreditCardRepository;

public final class CreditCardService {

    private final CreditCardRepository repository;

    public CreditCardService(final CreditCardRepository repository) {
        this.repository = repository;
    }

    /**
     *  Try to process payment for a given credit card id.
     *
     * @param id of credit card.
     * @param amount to be withdrew.
     * @return Either of failed message because credit card is not found,
     * or either message of failure if card is found but withdraw has failed,
     * or successful message upon successful withdraw.
     */
    public Either<String, Either<String, String>> processPayment(final Long id, final Double amount) {
        return repository.findById(id)
            .map(cc -> cc.withdraw(amount))
            .map(this::withdraw)
            .toEither("Credit Card with id = " + id + " not found.");
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
