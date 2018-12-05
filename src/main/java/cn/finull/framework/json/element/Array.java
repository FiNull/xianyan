package cn.finull.framework.json.element;

import cn.finull.framework.json.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * json数组
 */
public class Array extends Element {

    public Array() {
        setType(Type.ARRAY);
    }

    private List<Element> elementList = new ArrayList<>();

    public void add(Element element) {
        elementList.add(element);
    }

    public Element get(int index) {
        return elementList.get(index);
    }

    public int size() {
        return elementList.size();
    }

    public void forEarch(Consumer<Element> consumer) {
        elementList.forEach(consumer);
    }
}
