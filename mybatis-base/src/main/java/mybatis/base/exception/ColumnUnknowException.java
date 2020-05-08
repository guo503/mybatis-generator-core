package mybatis.base.exception;

/**
 * 未知的字段异常
 *
 * @author lgt
 * @date 2019/5/10 : 5:31 PM
 */
public class ColumnUnknowException extends RuntimeException {

    public ColumnUnknowException(String message) {
        super(message);
    }

    public ColumnUnknowException(String message, Throwable cause) {
        super(message, cause);
    }
}
