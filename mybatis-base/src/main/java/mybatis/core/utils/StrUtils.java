package mybatis.core.utils;

/**
 * 字符串工具类型
 *
 * @author: guos
 * @date: 2019/9/3$ 14:33$
 **/
public class StrUtils {

    public static boolean isEmpty(Object str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(Object str) {
        return str != null && !"".equals(str);
    }
}
