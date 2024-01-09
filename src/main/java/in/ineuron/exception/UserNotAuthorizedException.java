package in.ineuron.exception;

public class UserNotAuthorizedException extends Exception {
    public UserNotAuthorizedException(String message) {
        super(message);
    }
}
