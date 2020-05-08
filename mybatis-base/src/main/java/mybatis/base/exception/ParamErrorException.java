package mybatis.base.exception;


/**
 * 参数异常
 *
 * @author lgt
 * @date 2019/5/17 : 2:14 PM
 */
public class ParamErrorException extends RuntimeException {

    public ParamErrorException() {
    }

    public ParamErrorException(String message) {
        super(message);
    }

    public ParamErrorException(String message, Throwable cause) {
        super(message, cause);
    }
}
