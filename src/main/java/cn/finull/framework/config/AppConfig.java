package cn.finull.framework.config;

import cn.finull.framework.except.JSONParserException;
import cn.finull.framework.json.JSON;
import cn.finull.framework.json.JSONArray;
import cn.finull.framework.json.JSONObject;
import cn.finull.framework.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class AppConfig {

    private static final JSONObject JSON_OBJ;

    static {
        try (
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            AppConfig.class.getClassLoader().getResourceAsStream("application.json"),
                            "UTF-8"
                    )
            )
        ) {
            JSON_OBJ = JSON.parse(StringUtil.reader(reader)).getObject();
        } catch (JSONParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> getArray(String key) {
        List<String> list = new ArrayList<>();
        JSONArray array = JSON_OBJ.getArray(key);
        for (int i = 0; i < array.size(); i ++) {
            list.add(array.getString(i));
        }
        return list;
    }

    // 获得Bean仓库
    public static List<String> getRepertories() {
        return getArray("repertories");
    }

    // 获得处理函数
    public static List<String> getHandlers() {
        return getArray("handlers");
    }

    // 获得异常处理函数
    public static List<String> getAdvice() {
        return getArray("advice");
    }

    // 模板的前缀
    public static String getViewPrefix() {
        return JSON_OBJ.getObject("view").getString("prefix");
    }

    // 模板的后缀
    public static String getViewSuffix() {
        return JSON_OBJ.getObject("view").getString("suffix");
    }

    // 获得文件上传的路径
    public static String getUploadPath() {
        return JSON_OBJ.getObject("pic").getString("upload");
    }

    // 获得图片的前缀
    public static String getHttpPrefix() {
        return JSON_OBJ.getObject("pic").getString("prefix");
    }

    // 获取数据库驱动
    public static String getDBDriver() {
        return JSON_OBJ.getObject("db").getString("driver");
    }

    // 获取连接数据库的url
    public static String getDBUrl() {
        return JSON_OBJ.getObject("db").getString("url");
    }

    // 获取连接数据库的用户名
    public static String getDBUsername() {
        return JSON_OBJ.getObject("db").getString("username");
    }

    // 获取连接数据库的密码
    public static String getDBPassword() {
        return JSON_OBJ.getObject("db").getString("password");
    }

    // 数据池的个数
    public static int getDBPool() {
        return JSON_OBJ.getObject("db").getInt("pool");
    }

    // 获得dao层的包
    public static List<String> getDBDao() {
        JSONArray array =  JSON_OBJ.getObject("db").getArray("dao");
        List<String> list = new ArrayList<>();
        for (int i = 0 ; i < array.size(); i ++) {
            list.add(array.getString(i));
        }
        return list;
    }
}
