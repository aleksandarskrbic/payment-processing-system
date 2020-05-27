package order.generator.service.model;

public class Order {

    private String email;
    private double amount;

    public Order() {
    }

    public Order(final String email, final double amount) {
        this.email = email;
        this.amount = amount;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(final double amount) {
        this.amount = amount;
    }
}
