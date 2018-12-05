package cn.finull.framework.util;

import java.io.*;

/**
 * 字符串工具类
 */
public final class StringUtil {

    public static boolean isBlank(String value) {
        int len;
        if (value == null || (len = value.length()) == 0) {
            return true;
        }
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /**
     * 将浮点数转换为字符串，如果浮点数没有小数部分将转换为整数字符串
     * @param value 浮点数
     * @return 字符串
     */
    public static String toNumber(double value) {
        String num = String.valueOf(value);
        num = num.contains("e") || !num.endsWith(".0") ? num : String.valueOf((long)value);
        return num;
    }

    /**
     * 读取字符流中的字符串
     * @param reader 字符输入流
     * @return 字符串
     */
    public static String reader(BufferedReader reader) {

        StringBuilder content = new StringBuilder();
        reader.lines().map(s -> s + "\n").forEach(content::append);

        if (isBlank(content.toString()))
            return "";

        content.deleteCharAt(content.length() - 1); // 消除最后一个换行
        return content.toString();
    }

    /**
     * 驼峰命名转下划线
     * @param value 驼峰命名的字符串
     * @return 下划线字符串
     */
    public static String humpToUnderline(String value) {
        StringBuilder result = new StringBuilder();
        result.append(Character.toLowerCase(value.charAt(0)));
        for (char ch : value.substring(1).toCharArray()) {
            if (Character.isUpperCase(ch)) {
                result.append("_");
                result.append(Character.toLowerCase(ch));
            }
            else {
                result.append(ch);
            }
        }
        return result.toString();
    }

    /**
     * 下划线命名法转驼峰命名法
     * @param value 下划线命名法的字符串
     * @return 驼峰命名的字符串
     */
    public static String underlineToHump(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i ++) {
            if (value.charAt(i) == '_') {
                result.append(Character.toUpperCase(value.charAt(i + 1)));
                i ++;
            }
            else {
                result.append(value.charAt(i));
            }
        }
        return result.toString();
    }
}
