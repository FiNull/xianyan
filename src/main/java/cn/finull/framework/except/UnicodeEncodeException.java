package cn.finull.framework.except;

/**
 * unicode编码异常
 */
public class UnicodeEncodeException extends RuntimeException {
    public UnicodeEncodeException() {
    }

    public UnicodeEncodeException(String message) {
        super(message);
    }

    public UnicodeEncodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnicodeEncodeException(Throwable cause) {
        super(cause);
    }

    public UnicodeEncodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
