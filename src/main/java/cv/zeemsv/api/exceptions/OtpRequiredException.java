package cv.zeemsv.api.exceptions;

public class OtpRequiredException extends RuntimeException {
    public OtpRequiredException(String message) {
        super(message);
    }
}
