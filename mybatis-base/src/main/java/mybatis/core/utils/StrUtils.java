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

    public static String guessGetterName(String name, Class<?> type) {
        return Boolean.TYPE == type ? (name.startsWith("is") ? name : "is" + upperFirst(name)) : "get" + upperFirst(name);
    }

    public static String upperFirst(String src) {
        if (Character.isLowerCase(src.charAt(0))) {
            return 1 == src.length() ? src.toUpperCase() : Character.toUpperCase(src.charAt(0)) + src.substring(1);
        } else {
            return src;
        }
    }
}
