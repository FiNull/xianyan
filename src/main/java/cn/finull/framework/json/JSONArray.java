package cn.finull.framework.json;

import cn.finull.framework.json.element.Array;
import cn.finull.framework.json.element.Element;
import cn.finull.framework.json.element.JObject;
import cn.finull.framework.json.element.Value;

public class JSONArray {

    private Array array;

    JSONArray(Array array) {
        this.array = array;
    }

    public int size() {
        if (array == null) return 0;
        return array.size();
    }

    public String getString(int index) {
        Element element = array.get(index);
        if (element == null) return null;
        if (element.getType() == Type.STRING) {
            return ((Value)element).getString();
        }
        return null;
    }

    public Double getDouble(int index) {
        Element element = array.get(index);
        if (element == null) return null;
        if (element.getType() == Type.NUMBER) {
            return ((Value)element).getNumber();
        }
        return null;
    }

    public Integer getInt(int index) {
        Double num = getDouble(index);
        if (num == null) return null;
        return (int)num.doubleValue();
    }

    public Long getLong(int index) {
        Double num = getDouble(index);
        if (num == null) return null;
        return (long)num.doubleValue();
    }

    public Boolean getBool(int index) {
        Element element = array.get(index);
        if (element == null) return null;
        if (element.getType() == Type.BOOL) {
            return ((Value)element).getBool();
        }
        return null;
    }

    public JSONObject getObject(int index) {
        Element element = array.get(index);
        if (element == null) return null;
        if (element.getType() == Type.OBJECT) {
            return new JSONObject((JObject) element);
        }
        return null;
    }

    public JSONArray getArray(int index) {
        Element element = array.get(index);
        if (element == null) return null;
        if (element.getType() == Type.ARRAY) {
            return new JSONArray((Array) element);
        }
        return null;
    }
}
