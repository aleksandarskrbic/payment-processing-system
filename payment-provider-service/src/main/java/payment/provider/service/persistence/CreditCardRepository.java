package payment.provider.service.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.vavr.control.Option;
import payment.provider.service.model.CreditCard;

public final class CreditCardRepository {

    private final Map<Long, CreditCard> repository;

    public CreditCardRepository() {
        this.repository = new ConcurrentHashMap<>();
        loadData();
    }

    /**
     *  Try to find credit card by id.
     *
     * @param id Credit Card id.
     * @return Option of CreditCard.
     */
    public Option<CreditCard> findById(final Long id) {
        return Option.of(repository.get(id));
    }

    /**
     *  Try to find credit card by account id.
     *
     * @param accountId account id.
     * @return Option of CreditCard.
     */
    public Option<CreditCard> findByAccountId(final Long accountId) {
        final Optional<CreditCard> creditCardOptional = repository.values().stream().filter(c -> c.accountId == accountId).findFirst();
        return Option.ofOptional(creditCardOptional);
    }

    /**
     *  Try to save credit card.
     *
     * @param creditCard Credit Cart to be saved.
     */
    public void save(final CreditCard creditCard) {
       repository.put(creditCard.id, creditCard);
    }

    private void loadData() {
        repository.put(1L, new CreditCard(1L, 1L, "00123333011111", 100d));
        repository.put(2L, new CreditCard(2L, 2L, "23776652342222", 355d));
        repository.put(3L, new CreditCard(3L, 3L, "00855453534543", 999d));
    }
}
