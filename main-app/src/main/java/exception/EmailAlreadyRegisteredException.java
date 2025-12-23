package exception;

public class EmailAlreadyRegisteredException extends Exception {
    public EmailAlreadyRegisteredException(String email) {
        super("L'Email '" + email + "' è gia registrata nel sistema");
    }
}
