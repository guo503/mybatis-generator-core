package org.mybatis.generator.utils;

import org.mybatis.generator.constant.CommonConstant;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * author: guos
 * date: 2019/9/24 14:34
 **/
public class ExceptionUtils {

    /**
     * 生成异常信息
     * param exceptionPack
     * param prefix
     * param suffix
     * return
     */
    public static String generateCode(String exceptionName, String prefix, String suffix) {
        StringBuilder message = new StringBuilder("throw new ");
        if (StringUtility.stringHasValue(exceptionName)) {
            message.append(exceptionName);
        } else {
            message.append(CommonConstant.DEFAULT_EXCEPTION);
        }
        message.append("(\"");
        message.append(prefix);
        message.append("信息$)".replace("$", suffix + "\""));
        message.append(";");
        return message.toString();
    }
}
