package mybatis.base.exception;

public class ProviderCreateException extends RuntimeException {

    public ProviderCreateException(String message) {
        super(message);
    }

    public ProviderCreateException(String message, Throwable cause) {
        super(message, cause);
    }
}
