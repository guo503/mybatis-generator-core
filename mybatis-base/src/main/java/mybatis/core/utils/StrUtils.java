package mybatis.core.utils;

/**
 * 字符串工具类型
 *
 * @author: guos
 * @date: 2019/9/3$ 14:33$
 **/
public class StrUtils {


    public static void main(String[] args) {
        System.out.println("users".substring(0,"users".lastIndexOf("s")));
        System.out.println(toLowerCaseFirst("neqId".substring(3)));
    }

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

    /**
     * 首字母转小写
     * @param s
     * @return
     */
    public static String toLowerCaseFirst(String s) {
        if (Character.isLowerCase(s.charAt(0))) {
            return s;
        } else {
            return Character.toLowerCase(s.charAt(0)) + s.substring(1);
        }
    }
}
