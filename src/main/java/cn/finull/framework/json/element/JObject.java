package cn.finull.framework.json.element;

import cn.finull.framework.json.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * json对象
 */
public class JObject extends Element {

    public JObject() {
        setType(Type.OBJECT);
    }

    private Map<String,Element> objectMap = new HashMap<>();

    public void put(String key,Element value) {
        objectMap.put(key,value);
    }

    public Element get(String key) {
        return objectMap.get(key);
    }

    public void forEach(BiConsumer<String,Element> consumer) {
        objectMap.forEach(consumer);
    }
}
