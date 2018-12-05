package cn.finull.framework.util;

import cn.finull.framework.except.BadParameterException;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.*;

/**
 * 反射相关的工具类
 */
public final class ClassUtil {

    private static String FORMAT = DateUtil.DATE_TIME_FROMT;

    public static void setDateFromt(String format) {
        FORMAT = format;
    }

    private static Map<Character,String> CLASS_NAME = new HashMap<>();
    static {
        CLASS_NAME.put('I',int.class.getName());
        CLASS_NAME.put('S',short.class.getName());
        CLASS_NAME.put('B',byte.class.getName());
        CLASS_NAME.put('J',long.class.getName());
        CLASS_NAME.put('D',double.class.getName());
        CLASS_NAME.put('Z',boolean.class.getName());
    }

    public static <T> T copyObject(Map<String,Object> source,Class<T> clz) throws IllegalAccessException, InstantiationException, IntrospectionException, InvocationTargetException {
        Object obj = clz.newInstance();
        // 获得该类中的getter、setter方法
        BeanInfo beanInfo = Introspector.getBeanInfo(clz);
        // 获得该类中的属性描述
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {

            if ("class".equals(pd.getName())) {
                continue;
            }

            // 获得该字段的set方法
            Method method = pd.getWriteMethod();

            Object value = source.get(pd.getName());
            if (pd.getPropertyType().getSimpleName().equals("Integer")
                    && value.getClass().getSimpleName().equals("Long")) {
                value = ((Long)value).intValue();
            }

            if (pd.getPropertyType().getSimpleName().equals("Boolean")) {
                value = !String.valueOf(value).equals("0");
            }

            // 将值写入到这个方法中
            method.invoke(obj,value);
        }
        return (T) obj;
    }

    /**
     * 将map中的字段拷贝到一个对象中
     * @param source 将要拷贝的map
     * @param clz 拷贝到这个类的对象
     * @param <T> 类型
     * @return 一个对象
     */
    public static <T> T copyProperty(Map<String,String[]> source,Class<T> clz)
            throws IllegalAccessException, InstantiationException,
            IntrospectionException, InvocationTargetException {
        T t = clz.newInstance();
        // 获得该类中的getter、setter方法
        BeanInfo beanInfo = Introspector.getBeanInfo(clz);
        // 获得该类中的属性描述
        PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor pd : pds) {

            String key = pd.getName();
            if ("class".equals(key)) {
                continue;
            }

            // 获得map中该字段的值
            String[] values = source.get(pd.getName());

            if (values == null) continue;

            // 参数
            Object parameter = values;

            // 属性类型名称
            String propertyClassName = pd.getPropertyType().getName();
            if (propertyClassName.startsWith("[")) {
                // 类型是一个数组
                if (CLASS_NAME.containsKey(propertyClassName.charAt(1))) {
                    // 是基础类型
                    parameter = classConvert(CLASS_NAME.get(propertyClassName.charAt(1)),values);
                }
                else {
                    // 是包装类型或自定义类型
                    parameter = classConvert(propertyClassName.substring(2),values);
                }
            }
            else {
                // 类型是一个元素
                parameter = classConvert(propertyClassName,values[0]);
            }

            // 获得该字段的set方法
            Method method = pd.getWriteMethod();

            // 将值写入到这个方法中
            method.invoke(t,parameter);
        }
        return t;
    }

    private static Object classConvert(String className,String[] values) {
        List<Object> list = new ArrayList<>();
        for (String value : values) {
            list.add(classConvert(className,value));
        }
        return list.toArray();
    }

    /**
     * 将字符串转换为其他的类型
     * @param className 类型名称
     * @param value 字符串值
     * @return 转换后的值
     */
    private static Object classConvert(String className,String value) {
        Object param = value;
        // 转换为时间类型
        if (className.equals(Date.class.getName())) {
            if (value.matches("[0-9]+")) {
                param = new Date(Long.valueOf(value));
            }
            else {
                try {
                    param = DateUtil.parse(value,FORMAT);
                } catch (ParseException e) {
                    throw new BadParameterException(className + " is not a date type");
                }
            }
        }
        else if (className.equals(int.class.getName())
                || className.equals(Integer.class.getName())){
            param = Integer.valueOf(value);
        }
        else if (className.equals(long.class.getName())
                || className.equals(Long.class.getName())) {
            param = Long.valueOf(value);
        }
        else if (className.equals(boolean.class.getName())
                || className.equals(Boolean.class.getName())) {
            param = Boolean.valueOf(value);
        }
        else if (className.equals(double.class.getName())
                || className.equals(Double.class.getName())) {
            param = Boolean.valueOf(value);
        }
        else if (className.equals(short.class.getName())
                || className.equals(Short.class.getName())) {
            param = Short.valueOf(value);
        }
        else if (className.equals(byte.class.getName())
                || className.equals(Byte.class.getName())) {
            param = Byte.valueOf(value);
        }
        return param;
    }

    /**
     * 通过反射实例化一个对象
     */
    public static <T> T newInstance(Class<T> clz) {
        try {
            return clz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获得一个类的所有字段名和对应的值
     */
    public static Map<String,Object> getClassFieldNamesAndValues(Object param) {
        try {
            Map<String,Object> data = new TreeMap<>();
            Field[] fields = param.getClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                data.put(field.getName(), field.get(param));
            }
            return data;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获的类上的字段名
     * @param clz 类
     * @return 字段名集合
     */
    public static List<String> getClassFiledNames(Class clz) {
        List<String> fieldNames = new ArrayList<>();
        for (Field field : clz.getDeclaredFields()) {
            field.setAccessible(true);
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    /**
     * 为对象设置值
     * @param param 对象
     * @param fieldName 字段名
     * @param value 参数 只能是基础类型和String
     */
    public static void setValue(Object param,String fieldName,Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = param.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        Object tmpValue = value;
        if (field.getType().getSimpleName().equals("Integer")
                && value.getClass().getSimpleName().equals("Long")) {
            tmpValue = ((Long)value).intValue();
        }
        field.set(param,tmpValue);
    }
}
