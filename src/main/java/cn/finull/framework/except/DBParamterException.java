package cn.finull.framework.except;

public class DBParamterException extends RuntimeException {
    public DBParamterException() {
    }

    public DBParamterException(String message) {
        super(message);
    }

    public DBParamterException(String message, Throwable cause) {
        super(message, cause);
    }

    public DBParamterException(Throwable cause) {
        super(cause);
    }

    public DBParamterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
