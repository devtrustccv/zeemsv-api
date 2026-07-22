package cv.zeemsv.api.domain.external.business;

public class ProcessStartException extends RuntimeException {
    public ProcessStartException(String message) {
        super(message);
    }

    public ProcessStartException(String message, Throwable cause) {
        super(message, cause);
    }
}
