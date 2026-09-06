package sk.ajamrich.andrej_projekt_1.gateway.auth;

public class InvalidInviteCodeException extends RuntimeException {
    public InvalidInviteCodeException(String message) {
        super(message);
    }
}