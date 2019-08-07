package cn.finull.framework.core.request;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求地址及方法
 */
public class URI {

    private String method;
    private String uri;

    private Map<String, String> uriParams = new HashMap<>();

    public URI(String method, String uri) {
        this.method = method.toUpperCase();
        this.uri = uri;
    }

    @Override
    public int hashCode() {
        // jdk1.6及以上会自动优化为StringBuilder.append()
        // 故不会存在空指针
        return (method + ":" + uri).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof URI) {
            URI o = (URI) obj;
            if (!method.equalsIgnoreCase(o.method)) {
                return false;
            }
            String[] rests = uri.split("/");
            String[] uris = o.uri.split("/");
            if (rests.length != uris.length) {
                return false;
            }
            for (int i = 0; i < rests.length; i++) {
                if (rests[i].startsWith("{") && rests[i].endsWith("}")) {
                    o.uriParams.put(rests[i].replace("{", "").replace("}", ""), uris[i]);
                    continue;
                }
                if (rests[i].startsWith(":")) {
                    o.uriParams.put(rests[i].replace(":", ""), uris[i]);
                    continue;
                }
                if (!rests[i].equals(uris[i])) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    boolean contains(String path) {
        if (uri.equals(path)) {
            return true;
        }
        String[] rests = uri.split("/");
        String[] uris = path.split("/");
        if (rests.length != uris.length) {
            return false;
        }
        for (int i = 0; i < rests.length; i++) {
            if ((rests[i].startsWith("{") && rests[i].endsWith("}"))
                    || rests[i].startsWith(":")) {
                continue;
            }
            if (!rests[i].equals(uris[i])) {
                return false;
            }
        }
        return true;
    }

    public Map<String, String> getParams() {
        return uriParams;
    }
}
