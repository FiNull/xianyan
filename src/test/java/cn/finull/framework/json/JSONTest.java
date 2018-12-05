package cn.finull.framework.json;

import cn.finull.framework.except.JSONParserException;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

public class JSONTest {

    @Test
    void parse() throws JSONParserException {
        JSON json = JSON.parse("{\"username\":\"FiNull\",\"sex\":true,\"age\":18,\"course\":[{\"name\":\"Math\",\"time\":12},{\"name\":\"English\",\"time\":10}]}");
        System.out.println(json.toString());
    }

    @Test
    void format() {
        String password = "123456";
        String hashed = BCrypt.hashpw(password,BCrypt.gensalt());
        System.out.println(BCrypt.checkpw(password,hashed));
    }
}