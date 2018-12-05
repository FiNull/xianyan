package cn.finull.framework.except;

/**
 * unicode字符解码异常
 */
public class UnicodeDecodeException extends RuntimeException {
    public UnicodeDecodeException() {
    }

    public UnicodeDecodeException(String message) {
        super(message);
    }

    public UnicodeDecodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnicodeDecodeException(Throwable cause) {
        super(cause);
    }

    public UnicodeDecodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
