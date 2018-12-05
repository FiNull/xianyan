package cn.finull.framework.except;

/**
 * 将对象转换为json字符串异常
 */
public class JSONEncodeException extends RuntimeException {
    public JSONEncodeException() {
    }

    public JSONEncodeException(String message) {
        super(message);
    }

    public JSONEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public JSONEncodeException(Throwable cause) {
        super(cause);
    }

    public JSONEncodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
