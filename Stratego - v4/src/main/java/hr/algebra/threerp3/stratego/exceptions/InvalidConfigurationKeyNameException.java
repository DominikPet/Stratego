package hr.algebra.threerp3.stratego.exceptions;

public class InvalidConfigurationKeyNameException extends RuntimeException {

    public InvalidConfigurationKeyNameException() {
    }

    public InvalidConfigurationKeyNameException(String message) {
        super(message);
    }

    public InvalidConfigurationKeyNameException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigurationKeyNameException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigurationKeyNameException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
