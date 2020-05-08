package mybatis.base.exception;

public class EntityUnDefinedException extends RuntimeException {

    public EntityUnDefinedException(String message) {
        super(message);
    }

    public EntityUnDefinedException(String message, Throwable cause) {
        super(message, cause);
    }
}
