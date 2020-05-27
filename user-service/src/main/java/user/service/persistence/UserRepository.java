package user.service.persistence;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import io.vavr.control.Option;
import user.service.model.User;

public final class UserRepository {

    private final Map<Long, User> repository;

    public UserRepository() {
        this.repository = new ConcurrentHashMap<>();
        loadData();
    }

    /**
     *  Try to find user by id.
     *
     * @param id User id.
     * @return Option of User.
     */
    public Option<User> findByEmail(final Long id) {
        return Option.of(repository.get(id));
    }

    /**
     *  Try to find user by email.
     *
     * @param email user email.
     * @return Option of User.
     */
    public Option<User> findByEmail(final String email) {
        final Optional<User> userOptional = repository.values().stream().filter(u -> u.email.equals(email)).findFirst();
        return Option.ofOptional(userOptional);
    }


    private void loadData() {
        repository.put(1L, new User(1L, "John", "Doe", "john.doe@gmail.com"));
        repository.put(2L, new User(2L, "Jane", "Doe", "jane.doe@gmail.com"));
        repository.put(3L, new User(3L, "Jack", "Sparrow", "john@yahoo.com"));
    }
}
