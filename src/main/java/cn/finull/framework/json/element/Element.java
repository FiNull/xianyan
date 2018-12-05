package cn.finull.framework.json.element;

import cn.finull.framework.json.Type;

/**
 * 表示一个Json内的元素
 */
public class Element {
    private Type type = Type.NULL;

    public void setType(Type type) {
          this.type = type;
    }

    public Type getType() {
        return type;
    }
}
