package cn.finull.framework.core.response;

public class ResponseEntity<T> {

    private int status = HttpStatus.OK;

    private T data;

    public ResponseEntity() {
    }

    public ResponseEntity(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public ResponseEntity(T data) {
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
