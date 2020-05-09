package org.mybatis.generator.utils;

public class OsUtil {

    public static final String WINDOWS_FILE_SEP = "\\\\";

    public static final String LINUX_FILE_SEP = "/";

    private static final boolean IS_WINDOWS;

    static {
        String os = System.getProperty("os.name");
        IS_WINDOWS = os.toLowerCase().contains("win");
    }

    public static boolean isWindows() {
        return IS_WINDOWS;
    }

    public static String getFileSep(){
        return IS_WINDOWS ? WINDOWS_FILE_SEP : LINUX_FILE_SEP;
    }

    public static String castPath(String filePath) {
        String[] split = filePath.split(":");
        return OsUtil.LINUX_FILE_SEP + split[0].toLowerCase() + split[1].replaceAll(OsUtil.WINDOWS_FILE_SEP, OsUtil.LINUX_FILE_SEP);
    }

    public static void main(String[] args) {
        System.out.println(castPath("D:\\project\\generator2\\test.sh"));
    }
}
