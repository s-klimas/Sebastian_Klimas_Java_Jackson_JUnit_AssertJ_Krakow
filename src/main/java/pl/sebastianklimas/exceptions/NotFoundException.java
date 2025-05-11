package pl.sebastianklimas.exceptions;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("Element not found.");
    }

    public NotFoundException(String message) {
        super(message);
    }
}
