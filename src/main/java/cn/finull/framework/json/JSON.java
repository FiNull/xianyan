package cn.finull.framework.json;

import cn.finull.framework.except.JSONConvertException;
import cn.finull.framework.except.JSONParserException;
import cn.finull.framework.json.element.Array;
import cn.finull.framework.json.element.Element;
import cn.finull.framework.json.element.JObject;
import cn.finull.framework.json.element.Value;
import cn.finull.framework.util.DateUtil;
import cn.finull.framework.util.StringUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.util.*;

/**
 * 提供给用户的json类
 */
public class JSON {

    private JSON() {}

    private Element element;
    private String content;
    private JSONParser parser;

    /**
     * 将一个json字符串解析成为一个json
     * @param content 字符串
     * @return json
     */
    public static JSON parse(String content) throws JSONParserException {
        if (StringUtil.isBlank(content)) {
            return null;
        }
        JSONParser parser = new JSONParser();
        JSON json = new JSON();
        json.parser = parser;
        json.element = parser.parse(content);
        return json;
    }

    /**
     * 将一个java对象序列化为一个json
     * @param obj java对象
     * @return json
     */
    public static JSON format(Object obj) {
        JSONParser parser = new JSONParser();
        JSON json = new JSON();
        json.parser = parser;
        json.element = parser.format(obj);
        return json;
    }

    public String toString() {
        if (StringUtil.isBlank(content)) {
            content = parser.eleToString(element);
        }
        return content;
    }

    /**
     * 将json转换为对象，实行多退少补原则
     *     如果对象中需要的属性json中没有，则不进行赋值
     *     如果json中存在对象不需要的属性，也不会报错
     *     json的类型与对象的类型需要一一对应，否则会报错
     *          数字类型之间可以自动转换（不推荐）
     *          数字与string类型之间可以自动转换
     *          数字与日期类型之间可以自动转换
     *          string与日期类型之间可以自动转换
     *          尽管如此，我们仍旧推荐类型之间一一对应
     * @param clz class
     * @param <T> 类型
     * @return 对象
     */
    public <T> T to(Class<T> clz) {
        try {
            return to(clz,element);
        } catch (ParseException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON TO LIST
     * @param clz class
     * @param <T> 类型
     * @return list
     */
    public <T> List<T> toList(Class<T> clz) {
        List<T> list = new ArrayList<>();
        toCollection(clz,element,list);
        return list;
    }

    /**
     * JSON TO SET
     * @param clz class
     * @param <T> 类型
     * @return set
     */
    public <T> Set<T> toSet(Class<T> clz) {
        Set<T> set = new HashSet<>();
        toCollection(clz,element,set);
        return set;
    }

    private <T> void toCollection(Class<T> clz,Element ele,Collection<T> collection) {
        if (ele.getType() == Type.ARRAY) {
            Array array = (Array) ele;
            array.forEarch(el -> {
                try {
                    collection.add(to(clz,el));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private <T> T to(Class<T> clz,Element ele) throws ParseException, InstantiationException, IllegalAccessException {
        String typeName = clz.getName();

        if (typeName.equals(Collection.class.getName()) || typeName.equals(Map.class.getName())) {
            throw new JSONConvertException("不要在集合类型中嵌套集合");
        }

        if (typeName.equals(String.class.getName())) {
            return (T)getString(ele);
        }
        else if (typeName.equals(int.class.getName())) {
            Double num = getNumber(ele);
            return (T)Integer.valueOf(num == null ? 0 : (int)num.doubleValue());
        }
        else if (typeName.equals(Integer.class.getName())) {
            Double num = getNumber(ele);
            return (T)(num == null ? null : Integer.valueOf((int)num.doubleValue()));
        }
        else if (typeName.equals(byte.class.getName())) {
            Double num = getNumber(ele);
            return (T)Byte.valueOf(num == null ? (byte) 0 : (byte)num.doubleValue());
        }
        else if (typeName.equals(Byte.class.getName())) {
            Double num = getNumber(ele);
            return (T)(num == null ? null : Byte.valueOf((byte)num.doubleValue()));
        }
        else if (typeName.equals(short.class.getName())) {
            Double num = getNumber(ele);
            return (T)Short.valueOf(num == null ? (short)0 : (short)num.doubleValue());
        }
        else if (typeName.equals(Short.class.getName())) {
            Double num = getNumber(ele);
            return (T)(num == null ? null : Short.valueOf((short)num.doubleValue()));
        }
        else if (typeName.equals(double.class.getName())) {
            Double num = getNumber(ele);
            return (T)(num == null ? Double.valueOf(0.0) : num);
        }
        else if (typeName.equals(Double.class.getName())) {
            return (T)getNumber(ele);
        }
        else if (typeName.equals(long.class.getName())) {
            Double num = getNumber(ele);
            return (T)Long.valueOf(num == null ? 0L : (long)num.doubleValue());
        }
        else if (typeName.equals(Long.class.getName())) {
            Double num = getNumber(ele);
            return (T)(num == null ? null : Long.valueOf((long)num.doubleValue()));
        }
        else if (typeName.equals(boolean.class.getName())) {
            Boolean flag = getBool(ele);
            if (flag == null) {
                return (T)Boolean.valueOf(false);
            }
            return (T)flag;
        }
        else if (typeName.equals(Boolean.class.getName())) {
            return (T)getBool(ele);
        }
        else if (typeName.equals(Date.class.getName())) {
            return (T)getDate(ele);
        }
        else {
            return toObject(clz,ele);
        }
    }

    private <T> T toObject(Class<T> clz,Element ele)
            throws IllegalAccessException, InstantiationException, ParseException {
        if (ele.getType() != Type.OBJECT) {
            return null;
        }
        // 实例化该对象
        T t = clz.newInstance();
        JObject object = (JObject) ele;
        // 获得所有字段
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            // 字段名
            String name = field.getName();
            // 获取json的元素
            Element value = object.get(name);
            if (value == null) {
                continue;
            }
            // 字段类型
            Class fieldType = field.getType();
            String typeName = fieldType.getName();
            if (typeName.equals(List.class.getName())) {
                Class c = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                List list = new ArrayList();
                toCollection(c,value,list);
                field.set(t,list);
            }
            else if (typeName.equals(Set.class.getName())) {
                Class c = (Class) ((ParameterizedType)field.getGenericType()).getActualTypeArguments()[0];
                Set set = new HashSet();
                toCollection(c,value,set);
                field.set(t,set);
            }
            else {
                field.set(t,to(fieldType,value));
            }
        }
        return t;
    }

    private Date getDate(Element ele) throws ParseException {
        Value value = (Value) ele;
        if (ele.getType() == Type.STRING) {
            String s = value.getString();
            return DateUtil.parse(s,DateUtil.DATE_TIME_FROMT);
        }
        else if (ele.getType() == Type.NUMBER) {
            double d = value.getNumber();
            return new Date((long)d);
        }
        return null;
    }

    private String getString(Element ele) {
        Value value = (Value) ele;
        if (ele.getType() == Type.STRING) {
            return value.getString();
        }
        else if (ele.getType() == Type.NUMBER) {
            return StringUtil.toNumber(value.getNumber());
        }
        else if (ele.getType() == Type.BOOL) {
            return String.valueOf(value.getBool());
        }
        return null;
    }

    private Double getNumber(Element ele) {
        Value value = (Value) ele;
        if (ele.getType() == Type.NUMBER) {
            return value.getNumber();
        }
        else if (ele.getType() == Type.STRING) {
            try {
                return Double.valueOf(value.getString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        else if (ele.getType() == Type.BOOL) {
            boolean flag = value.getBool();
            return flag ? 1.0 : 0;
        }
        return null;
    }

    private Boolean getBool(Element ele) {
        Value value = (Value) ele;
        if (ele.getType() == Type.BOOL) {
            return value.getBool();
        }
        else if (ele.getType() == Type.STRING) {
            String bool = value.getString();
            if ("true".equals(bool) || "false".equals(bool)) {
                return Boolean.valueOf(bool);
            }
        }
        else if (ele.getType() == Type.NUMBER) {
            String num = StringUtil.toNumber(value.getNumber());
            if ("1".equals(num)) {
                return true;
            }
            if ("0".equals(num)) {
                return false;
            }
        }
        return null;
    }

    /**
     * 获得一个json对象
     * @return json对象
     */
    public JSONObject getObject() {
        if (element.getType() == Type.OBJECT) {
            return new JSONObject((JObject)element);
        }
        return null;
    }

    /**
     * 获得一个json array
     * @return json array
     */
    public JSONArray getArray() {
        if (element.getType() == Type.ARRAY) {
            return new JSONArray((Array)element);
        }
        return null;
    }

    public String getString() {
        if (element.getType() == Type.STRING) {
            return ((Value)element).getString();
        }
        return null;
    }

    public Double getNumber() {
        if (element.getType() == Type.STRING) {
            return ((Value)element).getNumber();
        }
        return null;
    }

    public Boolean getBool() {
        if (element.getType() == Type.BOOL) {
            return ((Value)element).getBool();
        }
        return null;
    }
}
