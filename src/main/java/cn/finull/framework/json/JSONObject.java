package cn.finull.framework.json;

import cn.finull.framework.json.element.Array;
import cn.finull.framework.json.element.Element;
import cn.finull.framework.json.element.JObject;
import cn.finull.framework.json.element.Value;

public class JSONObject {

    private JObject object;

    JSONObject(JObject object) {
        this.object = object;
    }

    public String getString(String key) {
        Element element = object.get(key);
        if (element == null) return null;
        if (element.getType() == Type.STRING) {
            return ((Value) element).getString();
        }
        return null;
    }

    public Double getDouble(String key) {
        Element element = object.get(key);
        if (element == null) return null;
        if (element.getType() == Type.NUMBER) {
            return ((Value) element).getNumber();
        }
        return null;
    }

    public Integer getInt(String key) {
        Double num = getDouble(key);
        if (num == null) return null;
        return (int) num.doubleValue();
    }

    public Long getLong(String key) {
        Double num = getDouble(key);
        if (num == null) return null;
        return (long) num.doubleValue();
    }

    public Boolean getBool(String key) {
        Element element = object.get(key);
        if (element == null) return null;
        if (element.getType() == Type.BOOL) {
            return ((Value) element).getBool();
        }
        return null;
    }

    public JSONObject getObject(String key) {
        Element element = object.get(key);
        if (element == null) return null;
        if (element.getType() == Type.OBJECT) {
            return new JSONObject((JObject) element);
        }
        return null;
    }

    public JSONArray getArray(String key) {
        Element element = object.get(key);
        if (element == null) return null;
        if (element.getType() == Type.ARRAY) {
            return new JSONArray((Array) element);
        }
        return null;
    }
}
