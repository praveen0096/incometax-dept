package io.mosip.credential.exception;

public class NotificationException extends BaseUncheckedException {

    public NotificationException(String errorMessage) {
        super(errorMessage);
    }

    public NotificationException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    public NotificationException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}
