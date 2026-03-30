package daos.utils;

public class LoginExistException extends RuntimeException{

    public LoginExistException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
