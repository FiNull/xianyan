package cn.finull.framework.util;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

public final class ObjectUtil {

    public static void copyObject(Object source, Object target) {
        Map<String, Object> sourceMap = ClassUtil.getClassFieldNamesAndValues(source);
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value = sourceMap.get(name);
            if (value == null) {
                continue;
            }
            try {
                // 将 Date 转换为 Long
                if (value instanceof Date && field.getType().getSimpleName().equals("Long")) {
                    value = ((Date) value).getTime();
                }
                if (!value.getClass().getSimpleName().equals(field.getType().getSimpleName())) {
                    continue;
                }
                field.set(target, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
