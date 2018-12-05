package cn.finull.framework.json.element;

/**
 * 表示一个json的值
 */
public class Value extends Element {

    private boolean bool;
    private String string;
    private double number;

    public boolean getBool() {
        return bool;
    }

    public void setBool(boolean bool) {
        this.bool = bool;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }

    public double getNumber() {
        return number;
    }

    public void setNumber(double number) {
        this.number = number;
    }
}
