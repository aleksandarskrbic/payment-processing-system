package payment.provider.service;

import org.junit.Test;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import payment.provider.service.model.CreditCard;
import payment.provider.service.persistence.CreditCardRepository;
import payment.provider.service.service.CreditCardService;

public class ServerTest {

    @Test
    public void asd() {
        final CreditCardRepository creditCardRepository = new CreditCardRepository();
        final CreditCardService creditCardService = new CreditCardService(creditCardRepository);
    }
}
