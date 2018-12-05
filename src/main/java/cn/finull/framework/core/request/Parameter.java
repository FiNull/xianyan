package cn.finull.framework.core.request;

import cn.finull.framework.config.AppConfig;
import cn.finull.framework.core.Media;
import cn.finull.framework.except.BadParameterException;
import cn.finull.framework.except.JSONParserException;
import cn.finull.framework.json.JSON;
import cn.finull.framework.json.JSONObject;
import cn.finull.framework.util.ClassUtil;
import cn.finull.framework.util.FileUtil;
import cn.finull.framework.util.HashIDUtil;
import cn.finull.framework.util.StringUtil;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import javax.servlet.http.*;
import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

/**
 * 封装请求参数
 */
public class Parameter {

    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;

    public Parameter(HttpServletRequest request,HttpServletResponse response) throws IOException {
        this.request = request;
        this.response = response;
        this.session = request.getSession();

        header = new Header();
        try {
            body = new Body();
        } catch (FileUploadException e) {
            throw new IOException(e);
        }
        pCookie = new PCookie();
        pSession = new Session();
        model  = new Model();
    }

    // 头部信息
    private Header header;
    // 请求体
    private Body body;
    // cookie信息
    private PCookie pCookie;
    // session信息
    private Session pSession;
    // 请求转发的数据模型
    private Model model;

    public Header header() {
        return header;
    }

    public Body body() {
        return body;
    }

    public PCookie cookie() {
        return pCookie;
    }

    public Session session() {
        return pSession;
    }

    public Model model() {
        return model;
    }

    // 设置响应码
    public void status(int code) {
        response.setStatus(code);
    }

    // 头部参数
    public class Header {

        private Map<String,String[]> params;

        // 通过参数名获取参数
        public String byName(String name) {
            return request.getHeader(name);
        }

        public int intByName(String name) {
            return request.getIntHeader(name);
        }

        public Date dateByName(String name) {
            return new Date(request.getDateHeader(name));
        }

        // 将参数封装到一个对象中
        public <T> T to(Class<T> clz) {
            if (params == null) {
                params = new HashMap<>();
                Enumeration<String> headerNames = request.getHeaderNames();
                while (headerNames.hasMoreElements()) {
                    String headerName = headerNames.nextElement();
                    List<String> list = new ArrayList<>();
                    Enumeration<String> headers = request.getHeaders(headerName);
                    while (headers.hasMoreElements()) {
                        list.add(headers.nextElement());
                    }
                    String[] hs = new String[list.size()];
                    params.put(headerName,list.toArray(hs));
                }
            }
            try {
                return ClassUtil.copyProperty(params,clz);
            } catch (Exception e) {
                throw new BadParameterException(e);
            }
        }

        public void addHeader(String name,String value) {
            response.addHeader(name, value);
        }
    }

    // 请求、响应体参数
    public class Body {

        private Map<String,String> params = new HashMap<>();

        // 文件
        private Map<String,TransFile> fileParams = new HashMap<>();

        private JSON json;
        private JSONObject object;

        public Body() throws FileUploadException, IOException {
            // 参数是json
            if (Media.APPLICATION_JSON.equals(request.getContentType())
                    || Media.APPLICATION_JSON_UTF_8.equals(request.getContentType())
                    || Media.TEXT_PLAIN.equals(request.getContentType())
                    || Media.TEXT_PLAIN_UTF_8.equals(request.getContentType())) {
                try (
                        BufferedReader reader= request.getReader()
                ) {
                    json = JSON.parse(StringUtil.reader(reader));
                    if (json != null)
                        object = json.getObject();
                } catch (JSONParserException e) {
                    throw new BadParameterException(e);
                }
            }
            // 如果包含文件上传
            else if (ServletFileUpload.isMultipartContent(request)) {
                FileItemIterator itemIterator = new ServletFileUpload().getItemIterator(request);
                while (itemIterator.hasNext()) {
                    FileItemStream fileItemStream = itemIterator.next();
                    String fieldName = fileItemStream.getFieldName();
                    // 此字段是参数
                    if (fileItemStream.isFormField()) {
                        try (
                                BufferedReader reader = new BufferedReader(
                                        new InputStreamReader(
                                                fileItemStream.openStream(),
                                                "UTF-8"
                                        )
                                )
                        ) {
                            params.put(fieldName,StringUtil.reader(reader));
                        }
                    }
                    // 此字段是文件
                    else {
                        String fileName = fileItemStream.getName();
                        String fileType = fileItemStream.getContentType();
                        fileParams.put(fieldName,
                                new TransFile(
                                        FileUtil.writerTempFile(
                                                fileItemStream.openStream(),
                                                fileName),
                                        fileName,
                                        fileType)
                        );
                    }
                }
            }
        }

        public void addParams(Map<String,String> params) {
            this.params.putAll(params);
        }

        public <T> T to(Class<T> clz) {
            if (json == null) {
                try {
                    return ClassUtil.copyProperty(request.getParameterMap(),clz);
                } catch (Exception e) {
                    throw new BadParameterException(e);
                }
            }
            return json.to(clz);
        }

        public <T> List<T> toList(Class<T> clz) {
            if (json == null) {
                return new ArrayList<>();
            }
            return json.toList(clz);
        }

        public TransFile byFile(String name) {
            return fileParams.get(name);
        }

        public String byName(String name) {
            String value;
            if (object == null) {
                value = request.getParameter(name);
                return value == null ? params.get(name) : value;
            }
            value = object.getString(name);
            return value == null ? params.get(name) : value;
        }

        public String[] arrayByName(String name) {
            return request.getParameterValues(name);
        }

        public int intByName(String name) {
            return Integer.valueOf(byName(name));
        }

        public long longByName(String name) {
            return Long.valueOf(byName(name));
        }

        public Date dateByName(String name) {
            return new Date(longByName(name));
        }

        public boolean boolByName(String name) {
            return Boolean.valueOf(byName(name));
        }
    }

    // cookie 的相关信息
    public class PCookie {
        public Cookie[] getCookies() {
            return request.getCookies();
        }

        public Cookie byName(String name) {
            for (Cookie cookie : request.getCookies()) {
                if (name.equals(cookie.getName())) {
                    return cookie;
                }
            }
            return null;
        }

        public void addCookie(Cookie cookie) {
            response.addCookie(cookie);
        }

        public void addCoolie(String name,String value,String domain,String path,int maxAge) {
            Cookie cookie = new Cookie(name, value);
            cookie.setDomain(domain);
            cookie.setPath(path);
            cookie.setMaxAge(maxAge);
            response.addCookie(cookie);
        }
    }

    // 封装session信息
    public class Session {
        public Object byName(String name) {
            return session.getAttribute(name);
        }

        public void addSession(String name,Object value) {
            session.setAttribute(name, value);
        }

        public Object get(String name) {
            return session.getAttribute(name);
        }

        public void remove(String name) {
            session.removeAttribute(name);
        }
    }

    // 封装请求转发的模型数据
    public class Model {
        public Object get(String name) {
            return request.getAttribute(name);
        }

        public void add(String name,Object value) {
            request.setAttribute(name, value);
        }
    }

    // 用于处理文件上传
    public class TransFile {

        private File file;

        private String fileName;
        private String fileType;
        private String fileSuffix;

        TransFile(File file,String fileName,String fileType) {
            this.file = file;
            this.fileName = fileName;
            this.fileType = fileType;
            fileSuffix = fileName.substring(fileName.indexOf("."));
        }

        public String upload() {

            String path = request.getServletContext().getRealPath("/") + AppConfig.getUploadPath();
            String name = HashIDUtil.encode(System.currentTimeMillis()) + getFileSuffix();

            File pathFile = new File(path);
            pathFile.setWritable(true);
            if (!pathFile.exists()) {
                pathFile.mkdirs();
            }

            File outFile = new File(pathFile,name);
            outFile.setWritable(true);

            try (
                    FileChannel reader = new FileInputStream(file).getChannel();
                    FileChannel writer = new FileOutputStream(outFile).getChannel()
            ) {
                MappedByteBuffer mbb = reader.map(FileChannel.MapMode.READ_ONLY, 0, reader.size());
                writer.write(mbb);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                file.delete();
            }

            return name;
        }

        public InputStream getInputStream() throws IOException {
            return new FileInputStream(file);
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        // 文件后缀名，包含"."
        public String getFileSuffix() {
            return fileSuffix;
        }
    }
}
