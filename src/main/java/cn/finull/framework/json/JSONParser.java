package cn.finull.framework.json;

import cn.finull.framework.except.JSONEncodeException;
import cn.finull.framework.except.JSONParserException;
import cn.finull.framework.json.element.Array;
import cn.finull.framework.json.element.Element;
import cn.finull.framework.json.element.JObject;
import cn.finull.framework.json.element.Value;
import cn.finull.framework.util.StringUtil;
import cn.finull.framework.util.UnicodeUtil;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * json语法解析器
 */
class JSONParser {

    private JSONContent content = new JSONContent();

    /**
     * 将字符串解析成为一个json元素
     *
     * @param json 字符串
     * @return json元素
     */
    Element parse(String json) throws JSONParserException {
        content.json = json;
        char ch = content.nextChar();
        while (Character.isWhitespace(ch)) {
            ch = content.nextChar();
        }
        switch (ch) {
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case '\"':
                return parseString();
            case 'n':
                return parseNull();
            case 't':
                return parseTrue();
            case 'f':
                return parseFalse();
            default:
                if ((ch >= '0' && ch <= '9') || ch == '-') {
                    return parseNumber(ch);
                }
                throw generatorException(content.json.substring(0, content.curIndex), "wrong syntax");
        }
    }

    // 解析对象
    private Element parseObject() throws JSONParserException {
        boolean end = false;
        // json 对象
        JObject object = new JObject();
        // 栈
        Deque<Element> stack = new ArrayDeque<>();
        while (content.hasChar()) {
            char ch = content.nextChar();
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch == '}') {
                if (stack.size() == 0) {
                    end = true;
                }
                if (stack.size() == 2) {
                    end = true;
                    Element value = stack.pollFirst();
                    Element key = stack.pollFirst();
                    if (key.getType() == Type.STRING) {
                        object.put(((Value) key).getString(), value);
                    }
                }
                break;
            }
            switch (ch) {
                case '{':
                    stack.offerFirst(parseObject());
                    break;
                case '[':
                    stack.offerFirst(parseArray());
                    break;
                case '\"':
                    stack.offerFirst(parseString());
                    break;
                case ':':
                    // key不是string或没有Key
                    if (stack.size() != 1 || stack.getFirst().getType() != Type.STRING) {
                        throw generatorException(content.json.substring(0, content.curIndex + 1), "wrong syntax before \'^\'");
                    }
                    break;
                case 'n':
                    stack.offerFirst(parseNull());
                    break;
                case 't':
                    stack.offerFirst(parseTrue());
                    break;
                case 'f':
                    stack.offerFirst(parseFalse());
                    break;
                case ',':
                    if (content.hasChar()) {
                        char c = '0';
                        for (int i = 1; i < content.json.length(); i++) {
                            c = content.json.charAt(content.curIndex + i);
                            if (!Character.isWhitespace(c)) {
                                break;
                            }
                        }
                        if (c == '\"' && stack.size() == 2) {
                            Element value = stack.pollFirst();
                            Element key = stack.pollFirst();
                            if (key.getType() == Type.STRING) {
                                object.put(((Value) key).getString(), value);
                                break;
                            }
                        }
                    }
                    throw generatorException(content.json.substring(0, content.curIndex), "wrong syntax");
                default:
                    if ((ch >= '0' && ch <= '9') || ch == '-') {
                        // push
                        stack.offerFirst(parseNumber(ch));
                        break;
                    }
                    throw generatorException(content.json.substring(0, content.curIndex + 1), "wrong syntax");
            }
        }
        if (!end) {
            throw generatorException(content.json, "wrong syntax before \'^\'");
        }
        return object;
    }

    // 解析数组
    private Element parseArray() throws JSONParserException {
        // 存放数组的元素
        Deque<Element> deque = new ArrayDeque<>();
        boolean end = false;
        while (content.hasChar()) {
            char ch = content.nextChar();
            if (Character.isWhitespace(ch)) {
                continue;
            }
            if (ch == ']') {
                end = true;
                break;
            }
            switch (ch) {
                case '\"':
                    deque.offerLast(parseString());
                    break;
                case '[':
                    deque.offerLast(parseArray());
                    break;
                case 'n':
                    deque.offerLast(parseNull());
                    break;
                case 't':
                    deque.offerLast(parseTrue());
                    break;
                case 'f':
                    deque.offerLast(parseFalse());
                    break;
                case '{':
                    deque.offerLast(parseObject());
                    break;
                case ',':
                    if (content.hasChar()) {
                        char c = content.json.charAt(content.curIndex + 1);
                        if (c == '\"' || c == '[' || c == '{'
                                || c == 'n' || c == 't' || c == 'f'
                                || (c >= '0' && c <= '9') || c == '-') {
                            break;
                        }
                    }
                    throw generatorException(content.json.substring(0, content.curIndex), "wrong syntax");
                default:
                    if ((ch >= '0' && ch <= '9') || ch == '-') {
                        deque.offerLast(parseNumber(ch));
                        break;
                    }
                    throw generatorException(content.json.substring(0, content.curIndex + 1), "wrong syntax " + ch);
            }
        }
        if (!end) {
            throw generatorException(content.json, "missing \']\' before \'^\'");
        }
        Array array = new Array();
        while (!deque.isEmpty()) {
            array.add(deque.pollFirst());
        }
        return array;
    }

    // 解析数字
    private Element parseNumber(char ch) throws JSONParserException {
        StringBuilder sb = new StringBuilder("" + ch);
        while (content.hasChar()) {
            ch = content.nextChar();
            if ((ch >= '0' && ch <= '9') || ch == '.' || ch == 'e') {
                sb.append(ch);
                continue;
            }
            content.last();
            break;
        }
        try {
            return number(sb.toString());
        } catch (Exception e) {
            throw generatorException(sb.toString(), "wrong number format before \'^\'");
        }
    }

    private Value number(String val) {
        double number = Double.valueOf(val);
        Value value = new Value();
        value.setType(Type.NUMBER);
        value.setNumber(number);
        return value;
    }

    // 解析false
    private Element parseFalse() throws JSONParserException {
        String bool = getAfterChar('f', 4);
        if ("false".equals(bool)) {
            Value value = new Value();
            value.setType(Type.BOOL);
            value.setBool(false);
            return value;
        }
        throw generatorException(bool, "wrong syntax before \'^\'");
    }

    // 解析true
    private Element parseTrue() throws JSONParserException {
        String bool = getAfterChar('t', 3);
        if ("true".equals(bool)) {
            Value value = new Value();
            value.setType(Type.BOOL);
            value.setBool(true);
            return value;
        }
        throw generatorException(bool, "wrong syntax before \'^\'");
    }

    // 解析null
    private Element parseNull() throws JSONParserException {
        String str = getAfterChar('n', 3);
        if ("null".equals(str)) {
            Element ele = new Element();
            ele.setType(Type.NULL);
            return ele;
        }
        throw generatorException(str, "wrong syntax before \'^\'");
    }

    private String getAfterChar(char first, int len) {
        StringBuilder sb = new StringBuilder("" + first);
        for (int i = 0; i < len && content.hasChar(); i++) {
            sb.append(content.nextChar());
        }
        return sb.toString();
    }

    // 解析字符串
    private Element parseString() throws JSONParserException {
        boolean end = false;
        StringBuilder sb = new StringBuilder();
        while (content.hasChar()) {
            char ch = content.nextChar();
            // 字符串结束
            if (ch == '\"') {
                end = true;
                break;
            }
            // 非法字符
            if (IllegalChar.contain(ch)) {
                throw generatorException(sb.toString(), "Illegal Character " + IllegalChar.getValue(ch) + " before \'^\'");
            }
            // 转义字符
            if (ch == '\\') {
                if (!content.hasChar()) {
                    throw generatorException(sb.toString(), "missing \'\"\' before \'^\'");
                }
                switch (content.nextChar()) {
                    case '\"':
                        sb.append('\"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        StringBuilder s = new StringBuilder("\\u");
                        for (int i = 0; i < 4; i++) {
                            if (!content.hasChar()) {
                                throw generatorException(sb.append(s).toString(), s + "is wrong character");
                            }
                            s.append(content.nextChar());
                        }
                        sb.append(UnicodeUtil.decode(s.toString()));
                        break;
                    default:
                        throw generatorException(sb.toString(), "wrong syntax before \'^\', please check you json");
                }
                continue;
            }
            // 合法字符
            sb.append(ch);
        }
        if (!end) {
            throw generatorException(sb.toString(), "missing \'\"\' before \'^\'");
        }
        Value value = new Value();
        value.setType(Type.STRING);
        value.setString(sb.toString());
        return value;
    }

    private JSONParserException generatorException(String value, String msg) {
        return new JSONParserException("[line:" + content.lineNum
                + " col:" + content.colNum + "] " + value + "^: " + msg);
    }

    /**
     * 将java对象解析成为一个json元素
     *
     * @param obj java对象
     * @return json元素
     */
    Element format(Object obj) {
        if (obj == null) {
            Element ele = new Element();
            ele.setType(Type.NULL);
            return ele;
        }

        Element ele = new Element();
        ele.setType(Type.NULL);

        if (obj instanceof Collection) { // 集合
            ele = formatCollection((Collection) obj);
        } else if (obj instanceof Map) { // Map集合
            ele = formatMap((Map) obj);
        } else if (obj instanceof String) { // String类型
            ele = formatString((String) obj);
        } else if (obj instanceof Boolean) { // Bool类型
            ele = formatBool((Boolean) obj);
        } else if (obj instanceof Integer
                || obj instanceof Byte
                || obj instanceof Short
                || obj instanceof Double
                || obj instanceof Long) { // 数字类型
            ele = formatNumber(obj);
        } else { // 自建类型
            try {
                ele = formatObject(obj);
            } catch (IntrospectionException |
                    IllegalAccessException |
                    InvocationTargetException e) {
                throw new JSONEncodeException(e);
            }
        }

        return ele;
    }

    // 格式化集合
    private Element formatCollection(Collection list) {
        Array array = new Array();
        list.forEach(o -> array.add(format(o)));
        return array;
    }

    // 格式化Map
    private Element formatMap(Map map) {
        JObject object = new JObject();
        map.forEach((key, val) -> {
            if (key == null || !(key instanceof String)) {
                throw new JSONEncodeException("Object convert to JSON failure");
            }
            object.put((String) key, format(val));
        });
        return object;
    }

    // 格式化数字
    private Element formatNumber(Object number) {
        return number(number.toString());
    }

    // 格式化bool值
    private Element formatBool(Boolean bool) {
        Value value = new Value();
        value.setType(Type.BOOL);
        value.setBool(bool);
        return value;
    }

    // 格式化字符串
    private Element formatString(String str) {
        Value value = new Value();
        value.setType(Type.STRING);
        value.setString(str);
        return value;
    }

    // 格式化对象
    private Element formatObject(Object obj) throws IntrospectionException,
            InvocationTargetException, IllegalAccessException {
        JObject ele = new JObject();
        BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
        // 获得该类中的属性描述
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {
            String key = pd.getName();
            if ("class".equals(key)) {
                continue;
            }
            Object val = pd.getReadMethod().invoke(obj);
            ele.put(key, format(val));
        }
        return ele;
    }

    /**
     * 将Json元素转换为字符串
     *
     * @param element json元素
     * @return 字符串
     */
    String eleToString(Element element) {
        eleQueue.offerLast(element);
        encodeJSON();
        StringBuilder sb = new StringBuilder();
        while (!jsonQueue.isEmpty()) {
            sb.append(jsonQueue.pollFirst());
        }
        return sb.toString();
    }

    // 存放元素的队列
    private Deque<Element> eleQueue = new ArrayDeque<>();
    // 存放字符的队列
    private Deque<String> jsonQueue = new ArrayDeque<>();

    private void encodeJSON() {
        if (eleQueue.isEmpty()) {
            return;
        }
        Element child = eleQueue.pollFirst();
        switch (child.getType()) {
            case NULL:
                jsonQueue.offerLast("null");
                break;
            case BOOL:
                Value bool = (Value) child;
                jsonQueue.offerLast(String.valueOf(bool.getBool()));
                break;
            case NUMBER:
                Value number = (Value) child;
                jsonQueue.offerLast(StringUtil.toNumber(number.getNumber()));
                break;
            case STRING:
                jsonQueue.offerLast("\"");
                Value str = (Value) child;
                jsonQueue.offerLast(encodeString(str.getString()));
                jsonQueue.offerLast("\"");
                break;
            case ARRAY:
                Array array = (Array) child;
                jsonQueue.offerLast("[");
                array.forEarch(e -> {
                    eleQueue.offerLast(e);
                    encodeJSON();
                    jsonQueue.offerLast(",");
                });
                if (",".equals(jsonQueue.getLast())) {
                    jsonQueue.pollLast();
                }
                jsonQueue.offerLast("]");
                break;
            case OBJECT:
                JObject object = (JObject) child;
                jsonQueue.offerLast("{");
                object.forEach((key, value) -> {
                    jsonQueue.offerLast("\"");
                    jsonQueue.offerLast(encodeString(key));
                    jsonQueue.offerLast("\":");
                    eleQueue.offerLast(value);
                    encodeJSON();
                    jsonQueue.offerLast(",");
                });
                if (",".equals(jsonQueue.getLast())) {
                    jsonQueue.pollLast();
                }
                jsonQueue.offerLast("}");
                break;
            default:
                throw new JSONEncodeException("Element convert to Json failure");
        }
    }

    // 将普通的字符串进行Json格式的编码
    private String encodeString(String value) {
        if (StringUtil.isBlank(value)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '\"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    if (i + 1 < value.length()) {
                        char c = value.charAt(i + 1);
                        if (c == '\"' || c == '\\' || c == '/' || c == 'b'
                                || c == 'f' || c == 'n' || c == 'r'
                                || c == 't' || c == 'u') {
                            sb.append(ch).append(c);
                            i++;
                            break;
                        }
                    }
                    sb.append("\\\\");
                    break;
                default:
                    if (IllegalChar.contain(ch)) {
                        sb.append(IllegalChar.getValue(ch));
                        break;
                    }
                    sb.append(UnicodeUtil.encode(ch));
            }
        }
        return sb.toString();
    }

    private class JSONContent {

        int curIndex = -1;      // 当前游标位置
        int lastLineNum = 1;
        int lineNum = 1;        // 行数
        int lastColNum = 0;
        int colNum = 0;         // 列数
        String json;            // json字符串

        // 判断是否还有下一个字符串
        boolean hasChar() {
            return curIndex < (json.length() - 1);
        }

        // 获得下一个字符串
        char nextChar() {
            lastColNum = colNum;
            colNum++;
            char ch = json.charAt(++curIndex);
            if (ch == '\n') {
                lastLineNum = lineNum;
                lineNum++;
                colNum = 0;
            }
            return ch;
        }

        boolean last() {
            if (curIndex < 0) {
                return false;
            }
            curIndex--;
            lineNum = lastLineNum;
            colNum = lastColNum;
            return true;
        }
    }

    /**
     * 非法字符集
     */
    private static class IllegalChar {
        private static Map<Character, String> CHAR_MAP = new HashMap<>();

        static {
//            CHAR_MAP.put('/',"\\/");
            CHAR_MAP.put('\b', "\\b");
            CHAR_MAP.put('\f', "\\f");
            CHAR_MAP.put('\t', "\\t");
            CHAR_MAP.put('\n', "\\n");
            CHAR_MAP.put('\r', "\\r");
        }

        static boolean contain(char ch) {
            return CHAR_MAP.containsKey(ch);
        }

        static String getValue(char ch) {
            return CHAR_MAP.get(ch);
        }
    }
}