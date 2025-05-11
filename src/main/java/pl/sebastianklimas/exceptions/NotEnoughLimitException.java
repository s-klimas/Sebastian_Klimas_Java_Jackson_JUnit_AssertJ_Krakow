package pl.sebastianklimas.exceptions;

public class NotEnoughLimitException extends RuntimeException {
    public NotEnoughLimitException() {
        super("Can't pay for orders");
    }

    public NotEnoughLimitException(String message) {
        super(message);
    }
}
