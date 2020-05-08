package mybatis.base.helper;


import mybatis.core.annotation.NameStyle;

import java.util.Objects;

/**
 * 列名解析器
 *
 * @author lgt
 * @date 2019/4/30 : 11:08 AM
 */
public class ColumnNameParserFactory {

    private static final String UNDERLINE = "_";


    public static NameParser getParser(NameStyle nameStyle) {
        switch (nameStyle) {
            case camelhumpAndLowercase:
                return new CamelHumpAndLowercaseNameParser();
            case normal:
                return new NormalNameParser();
            default:
                return new NormalNameParser();
        }
    }

    static class CamelHumpAndLowercaseNameParser implements NameParser {

        @Override
        public String parse(String name) {
            if (Objects.isNull(name)) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            int startIdx = 0;
            for (int i = 0; i < name.length(); i++) {
                if (Character.isUpperCase(name.charAt(i))) {
                    sb.append(name, startIdx, i);
                    sb.append(UNDERLINE);
                    startIdx = i;
                }
            }
            sb.append(name, startIdx, name.length());
            return sb.toString().toLowerCase();
        }
    }

    static class NormalNameParser implements NameParser {

        @Override
        public String parse(String name) {
            if (Objects.isNull(name)) {
                return null;
            }
            return name;
        }
    }


}
