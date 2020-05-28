package payment.processor.service.adapter.http.user.dto;

public final class User {

    public final Long id;
    public final String firstName;
    public final String lastName;
    public final String email;

    public User(
        final Long id,
        final String firstName,
        final String lastName,
        final String email
    ) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }
}
