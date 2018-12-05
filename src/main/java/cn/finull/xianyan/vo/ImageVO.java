package cn.finull.xianyan.vo;

import java.util.Map;

public class ImageVO {
    private Integer code;
    private String msg;
    // src // 图片路径
    // title // 图片名称
    private Map<String,String> data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
