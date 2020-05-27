package user.service;

import org.junit.Before;
import org.junit.Test;
import io.vavr.control.Either;
import io.vavr.control.Either.Left;
import io.vavr.control.Either.Right;
import io.vavr.control.Option;
import io.vavr.control.Option.None;
import io.vavr.control.Option.Some;
import user.service.model.CreditCard;
import user.service.persistence.CreditCardRepository;
import user.service.service.CreditCardService;

import static org.assertj.core.api.Assertions.assertThat;

public final class CreditCardBusinessLogicTest {

    private CreditCardRepository creditCardRepository;
    private CreditCardService creditCardService;

    @Before
    public void setUp() {
        creditCardRepository = new CreditCardRepository();
        creditCardService =new CreditCardService(creditCardRepository);
    }

    @Test
    public void creditCardShouldBePresent() {
        // Given
        final Long validCreditCardId = 1L;

        // When
        final Option<CreditCard> creditCardOption = creditCardRepository.findById(validCreditCardId);

        // Then
        assertThat(creditCardOption).isInstanceOf(Some.class);
    }

    @Test
    public void userShouldNotBePresent() {
        // Given
        final Long invalidCreditCardId = 10L;

        // When
        final Option<CreditCard> creditCardOption = creditCardRepository.findById(invalidCreditCardId);

        // Then
        assertThat(creditCardOption).isInstanceOf(None.class);
    }

    @Test
    public void paymentShouldBeSuccessful() {
        // Given
        final Long creditCardId = 1L;

        // When
        final Either<String, Either<String, String>> result = creditCardService.processPayment(creditCardId, 100d);
        final CreditCard creditCard = creditCardRepository.findById(1L).get();
        final Either<String, String> paymentResult = result.get();

        // Then
        assertThat(paymentResult).isInstanceOf(Right.class);
        assertThat(creditCard.balance).isEqualTo(0L);
    }

    @Test
    public void paymentShouldFail() {
        // Given
        final Long creditCardId = 1L;

        // When
        final Either<String, Either<String, String>> result = creditCardService.processPayment(creditCardId, 110d);
        final Either<String, String> paymentResult = result.get();

        // Then
        assertThat(paymentResult).isInstanceOf(Left.class);
    }
}
