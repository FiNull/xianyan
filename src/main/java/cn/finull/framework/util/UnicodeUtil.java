package cn.finull.framework.util;

import cn.finull.framework.except.UnicodeDecodeException;
import cn.finull.framework.except.UnicodeEncodeException;

/**
 * unicode(\\uXXXX) 编解码工具类
 */
public final class UnicodeUtil {

    /**
     * 将转义的unicode字符转化为unicode字符
     * @param s 转义的unicode字符
     * @return unicode字符
     */
    public static char decode(String s) {
        if (s.length() != 6) {
            throw new UnicodeDecodeException(s + " is not unicode character");
        }
        // 判断字符串中第一个和第二个字符分别是 \ 和 u
        if (s.charAt(0) == '\\' && s.charAt(1) == 'u') {
            char code = 0;
            // 分别获得4个16进制数
            for (int i = 2; i < s.length(); i ++) {
                // 将其转换为小写字母
                char ch = s.charAt(i);
                // 保证其为一个16进制数
                if (is0XNumber(ch)) {
                    code <<= 4;
                    code |= Character.digit(ch,16);
                    continue;
                }
                throw new UnicodeDecodeException(s + " is not unicode character");
            }
            return code;
        }
        throw new UnicodeDecodeException(s + " is not unicode character");
    }

    // 是否为一个16进制数字
    private static boolean is0XNumber(char ch) {
        return (ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f'
                || (ch >= 'A' && ch <= 'F'));
    }

    /**
     * 将unicode字符进行转义
     * @param s unicode字符
     * @return 转义后的unicode字符
     */
    public static String encode(char s) {
        if (s < 256) {
            return String.valueOf(s);
        }
        char c = 0Xf;
        char ch1 = Character.forDigit((s & c << 12) >> 12,16);
        char ch2 = Character.forDigit((s & c << 8) >> 8,16);
        char ch3 = Character.forDigit((s & c << 4) >> 4,16);
        char ch4 = Character.forDigit(s & c,16);
        if (is0XNumber(ch1) && is0XNumber(ch2) && is0XNumber(ch3) && is0XNumber(ch4)) {
            return "\\u" + ch1 + ch2 + ch3 + ch4;
        }
        throw new UnicodeEncodeException(s + " is not unicode character");
    }
}
