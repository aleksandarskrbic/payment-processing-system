package user.service.service;

import io.vavr.control.Either;
import user.service.model.User;
import user.service.persistence.UserRepository;

public final class UserService {

    private final UserRepository repository;

    public UserService(final UserRepository repository) {
        this.repository = repository;
    }

    /**
     *  Try to find user by email.
     *
     * @param email User email.
     * @return Message of failure or User if it's present.
     */
    public Either<String, User> findByEmail(final String email) {
        return repository.findByEmail(email).toEither("User not found.");
    }
}
