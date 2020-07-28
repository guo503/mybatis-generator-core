package mybatis.base.template.controller.response;


import mybatis.base.template.controller.enums.ErrorCodeEnum;
import mybatis.base.template.controller.enums.IErrorCode;

import java.io.Serializable;

/**
 * API返回类
 *
 * @author guos
 * @date 2018年1月11日
 */
public class Result<T> implements Serializable {

    private final static long serialVersionUID = 1L;
    /**
     * 错误码
     */
    private int errorCode;
    /**
     * 错误提示
     */
    private String errorMessage;
    /**
     * 数据
     */
    private T data;
    /**
     * 总数
     */
    private Integer total;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.setErrorCode(ErrorCodeEnum.OK.getCode());
        return result;
    }

    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<T>();
        result.setErrorCode(ErrorCodeEnum.OK.getCode());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> success(T data, int total) {
        Result<T> result = new Result<T>();
        result.setErrorCode(ErrorCodeEnum.OK.getCode());
        result.setData(data);
        result.setTotal(total);
        return result;
    }

    public static <T> Result<T> fail() {
        Result<T> result = new Result<T>();
        result.setErrorCode(ErrorCodeEnum.UNDEFINE_ERROR.getCode());
        result.setErrorMessage(ErrorCodeEnum.UNDEFINE_ERROR.getMessage());
        return result;
    }

    public static <T> Result<T> fail(IErrorCode errorCode) {
        Result<T> result = fail();
        result.setErrorCode(errorCode.getCode());
        result.setErrorMessage(errorCode.getMessage());
        return result;
    }


    public static <T> Result<T> fail(String errorMessage) {
        Result<T> result = new Result<T>();
        result.setErrorCode(ErrorCodeEnum.UNDEFINE_ERROR.getCode());
        result.setErrorMessage(errorMessage);
        return result;
    }

    public static <T> Result<T> fail(ErrorCodeEnum errorCodeEnum) {
        Result<T> result = new Result<T>();
        result.setErrorCode(errorCodeEnum.getCode());
        result.setErrorMessage(errorCodeEnum.getMessage());
        return result;
    }

    public boolean isSuccess() {
        return ErrorCodeEnum.OK.getCode() == errorCode;
    }


    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "Result [errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", data=" + data + ", total="
                + total + "]";
    }


}
